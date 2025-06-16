package com.example.portal.service;

import com.example.portal.dto.BatchUploadRequest;
import com.example.portal.dto.BatchUploadResponse;
import com.example.portal.dto.FileUploadStatus;
import com.example.portal.dto.UploadPriority;
import com.example.portal.entity.PostFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatchUploadService {
    private final PostFileService postFileService;
    private final FileUploadProgressService progressService;
    private final FileUploadRetryService retryService;
    private final FileUploadPauseService pauseService;
    private final ImageCompressor imageCompressor;
    private final UploadRateLimitService rateLimitService;
    private final UploadPriorityService priorityService;

    private final Map<String, BatchUploadResponse> batchUploads = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public BatchUploadResponse startBatchUpload(BatchUploadRequest request, List<MultipartFile> files) {
        String batchId = UUID.randomUUID().toString();
        BatchUploadResponse response = BatchUploadResponse.of(batchId, files.size());
        batchUploads.put(batchId, response);

        // 파일 우선순위 계산 및 정렬
        List<MultipartFile> prioritizedFiles = prioritizeFiles(batchId, files);

        switch (BatchUploadRequest.UploadStrategy.valueOf(request.getUploadStrategy())) {
            case SEQUENTIAL:
                processSequentialUpload(batchId, request, prioritizedFiles);
                break;
            case PARALLEL:
                processParallelUpload(batchId, request, prioritizedFiles);
                break;
            case PRIORITY:
                processPriorityUpload(batchId, request, prioritizedFiles);
                break;
        }

        return response;
    }

    private List<MultipartFile> prioritizeFiles(String batchId, List<MultipartFile> files) {
        // 각 파일의 우선순위 계산
        Map<MultipartFile, UploadPriority> filePriorities = new HashMap<>();
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String uploadId = batchId + "_" + i;
            UploadPriority priority = priorityService.calculatePriority(uploadId, file);
            filePriorities.put(file, priority);
        }

        // 우선순위에 따라 파일 정렬
        return files.stream()
                .sorted(Comparator.comparingInt(file -> filePriorities.get(file).getPriority()).reversed())
                .collect(Collectors.toList());
    }

    private void processSequentialUpload(String batchId, BatchUploadRequest request, List<MultipartFile> files) {
        executorService.submit(() -> {
            try {
                for (int i = 0; i < files.size(); i++) {
                    MultipartFile file = files.get(i);
                    String uploadId = batchId + "_" + i;

                    if (request.isCompressImages() && isImageFile(file)) {
                        file = imageCompressor.compressImage(file);
                    }

                    try {
                        // 속도 제한 적용
                        rateLimitService.initializeRateLimiter(uploadId, request.getMaxConcurrentUploads());

                        PostFile savedFile = postFileService.saveFile(file, request.getPostId());
                        updateBatchProgress(batchId, i + 1, 0);
                    } catch (Exception e) {
                        handleUploadError(batchId, i, e.getMessage());
                    } finally {
                        rateLimitService.removeRateLimiter(uploadId);
                        priorityService.removePriority(uploadId);
                    }
                }
                updateBatchStatus(batchId, "COMPLETED");
            } catch (Exception e) {
                updateBatchError(batchId, e.getMessage());
            }
        });
    }

    private void processParallelUpload(String batchId, BatchUploadRequest request, List<MultipartFile> files) {
        int maxConcurrent = Math.min(request.getMaxConcurrentUploads(), files.size());
        CountDownLatch latch = new CountDownLatch(files.size());
        AtomicInteger completedCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);

        for (int i = 0; i < files.size(); i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    MultipartFile file = files.get(index);
                    String uploadId = batchId + "_" + index;

                    if (request.isCompressImages() && isImageFile(file)) {
                        file = imageCompressor.compressImage(file);
                    }

                    try {
                        // 속도 제한 적용
                        rateLimitService.initializeRateLimiter(uploadId, request.getMaxConcurrentUploads());

                        PostFile savedFile = postFileService.saveFile(file, request.getPostId());
                        completedCount.incrementAndGet();
                    } catch (Exception e) {
                        failedCount.incrementAndGet();
                        handleUploadError(batchId, index, e.getMessage());
                    } finally {
                        latch.countDown();
                        updateBatchProgress(batchId, completedCount.get(), failedCount.get());
                        rateLimitService.removeRateLimiter(uploadId);
                        priorityService.removePriority(uploadId);
                    }
                } catch (Exception e) {
                    latch.countDown();
                    updateBatchError(batchId, e.getMessage());
                }
            });
        }

        try {
            latch.await();
            updateBatchStatus(batchId, "COMPLETED");
        } catch (InterruptedException e) {
            updateBatchError(batchId, "Upload interrupted");
        }
    }

    private void processPriorityUpload(String batchId, BatchUploadRequest request, List<MultipartFile> files) {
        PriorityBlockingQueue<UploadTask> taskQueue = new PriorityBlockingQueue<>();

        // 파일 크기와 우선순위에 따라 작업 큐에 추가
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String uploadId = batchId + "_" + i;
            UploadPriority priority = priorityService.getPriority(uploadId);
            taskQueue.offer(new UploadTask(i, file, file.getSize(), priority.getPriority()));
        }

        int totalFiles = files.size();
        AtomicInteger completedCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);

        while (!taskQueue.isEmpty()) {
            UploadTask task = taskQueue.poll();
            try {
                if (request.isCompressImages() && isImageFile(task.file)) {
                    task.file = imageCompressor.compressImage(task.file);
                }

                // 속도 제한 적용
                String uploadId = batchId + "_" + task.index;
                rateLimitService.initializeRateLimiter(uploadId, request.getMaxConcurrentUploads());

                PostFile savedFile = postFileService.saveFile(task.file, request.getPostId());
                completedCount.incrementAndGet();
            } catch (Exception e) {
                failedCount.incrementAndGet();
                handleUploadError(batchId, task.index, e.getMessage());
            } finally {
                updateBatchProgress(batchId, completedCount.get(), failedCount.get());
                rateLimitService.removeRateLimiter(batchId + "_" + task.index);
                priorityService.removePriority(batchId + "_" + task.index);
            }
        }

        updateBatchStatus(batchId, "COMPLETED");
    }

    private void updateBatchProgress(String batchId, int completed, int failed) {
        BatchUploadResponse response = batchUploads.get(batchId);
        if (response != null) {
            double progress = (double) completed / response.getTotalFiles() * 100;
            response.updateProgress(completed, failed, progress);
        }
    }

    private void updateBatchStatus(String batchId, String status) {
        BatchUploadResponse response = batchUploads.get(batchId);
        if (response != null) {
            response.updateStatus(status);
        }
    }

    private void updateBatchError(String batchId, String errorMessage) {
        BatchUploadResponse response = batchUploads.get(batchId);
        if (response != null) {
            response.updateError(errorMessage);
        }
    }

    private void handleUploadError(String batchId, int fileIndex, String errorMessage) {
        BatchUploadResponse response = batchUploads.get(batchId);
        if (response != null) {
            Map<String, FileUploadStatus> statuses = response.getFileStatuses();
            if (statuses == null) {
                statuses = new HashMap<>();
                response.setFileStatuses(statuses);
            }
            statuses.put(String.valueOf(fileIndex), FileUploadStatus.FAILED);

            List<String> failedFiles = response.getFailedFileNames();
            if (failedFiles == null) {
                failedFiles = new ArrayList<>();
                response.setFailedFileNames(failedFiles);
            }
            failedFiles.add(String.valueOf(fileIndex));
        }
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    public BatchUploadResponse getBatchStatus(String batchId) {
        return batchUploads.get(batchId);
    }

    public void cancelBatchUpload(String batchId) {
        BatchUploadResponse response = batchUploads.get(batchId);
        if (response != null) {
            response.updateStatus("CANCELLED");
            // 관련된 모든 업로드 작업 취소
            for (int i = 0; i < response.getTotalFiles(); i++) {
                String uploadId = batchId + "_" + i;
                progressService.cancelUpload(uploadId);
                retryService.clearRetryInfo(uploadId);
                pauseService.clearPauseInfo(uploadId);
            }
        }
    }

    private static class UploadTask implements Comparable<UploadTask> {
        final int index;
        MultipartFile file;
        final long size;
        final int priority;

        UploadTask(int index, MultipartFile file, long size, int priority) {
            this.index = index;
            this.file = file;
            this.size = size;
            this.priority = priority;
        }

        @Override
        public int compareTo(UploadTask other) {
            int priorityCompare = Integer.compare(other.priority, this.priority);
            if (priorityCompare != 0) {
                return priorityCompare;
            }
            return Long.compare(this.size, other.size);
        }
    }
}