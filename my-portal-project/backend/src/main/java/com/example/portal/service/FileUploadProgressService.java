package com.example.portal.service;

import com.example.portal.dto.FileUploadProgress;
import com.example.portal.dto.FileUploadStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FileUploadProgressService {
    private final Map<String, FileUploadProgress> progressMap = new ConcurrentHashMap<>();
    private final Map<String, Boolean> cancellationMap = new ConcurrentHashMap<>();

    public void updateProgress(String uploadId, FileUploadProgress progress) {
        if (isCancelled(uploadId)) {
            progress = FileUploadProgress.builder()
                    .fileName(progress.getFileName())
                    .status(FileUploadStatus.CANCELLED)
                    .build();
        }
        progressMap.put(uploadId, progress);
    }

    public FileUploadProgress getProgress(String uploadId) {
        return progressMap.get(uploadId);
    }

    public void removeProgress(String uploadId) {
        progressMap.remove(uploadId);
        cancellationMap.remove(uploadId);
    }

    public void cancelUpload(String uploadId) {
        cancellationMap.put(uploadId, true);
        FileUploadProgress currentProgress = progressMap.get(uploadId);
        if (currentProgress != null) {
            updateProgress(uploadId, FileUploadProgress.builder()
                    .fileName(currentProgress.getFileName())
                    .status(FileUploadStatus.CANCELLED)
                    .build());
        }
    }

    public boolean isCancelled(String uploadId) {
        return Boolean.TRUE.equals(cancellationMap.get(uploadId));
    }

    public boolean canResume(String uploadId) {
        FileUploadProgress progress = progressMap.get(uploadId);
        return progress != null && !isCancelled(uploadId);
    }

    public boolean canRestart(String uploadId) {
        FileUploadProgress progress = progressMap.get(uploadId);
        return progress != null && progress.getStatus() == FileUploadStatus.FAILED;
    }
}