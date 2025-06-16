package com.example.portal.service;

import com.example.portal.config.FileUploadConfig;
import com.example.portal.dto.FileUploadProgress;
import com.example.portal.dto.FileUploadRetry;
import com.example.portal.dto.FileUploadStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class FileUploadRetryService {
    private final FileUploadConfig uploadConfig;
    private final FileUploadProgressService progressService;
    private final Map<String, FileUploadRetry> retryMap = new ConcurrentHashMap<>();

    public FileUploadRetry initializeRetry(String uploadId) {
        FileUploadRetry retry = FileUploadRetry.of(uploadId);
        retryMap.put(uploadId, retry);
        return retry;
    }

    public FileUploadRetry getRetryInfo(String uploadId) {
        return retryMap.get(uploadId);
    }

    public boolean canRetry(String uploadId) {
        FileUploadRetry retry = retryMap.get(uploadId);
        if (retry == null) {
            return false;
        }
        return retry.getRetryCount() < uploadConfig.getMaxRetries();
    }

    public FileUploadRetry handleUploadError(String uploadId, String errorMessage) {
        FileUploadRetry retry = retryMap.get(uploadId);
        if (retry == null) {
            retry = FileUploadRetry.of(uploadId);
        }

        if (canRetry(uploadId)) {
            retry = retry.incrementRetryCount(errorMessage);
            retryMap.put(uploadId, retry);

            // 진행률 업데이트
            progressService.updateProgress(uploadId, FileUploadProgress.builder()
                    .fileName(retry.getUploadId())
                    .status(FileUploadStatus.FAILED)
                    .errorMessage("재시도 중... (" + retry.getRetryCount() + "/" + uploadConfig.getMaxRetries() + ")")
                    .build());
        } else {
            // 최대 재시도 횟수 초과
            progressService.updateProgress(uploadId, FileUploadProgress.builder()
                    .fileName(retry.getUploadId())
                    .status(FileUploadStatus.FAILED)
                    .errorMessage("최대 재시도 횟수 초과: " + errorMessage)
                    .build());
            retryMap.remove(uploadId);
        }

        return retry;
    }

    public void updateUploadProgress(String uploadId, long bytesUploaded) {
        FileUploadRetry retry = retryMap.get(uploadId);
        if (retry != null) {
            retry = retry.updateProgress(bytesUploaded);
            retryMap.put(uploadId, retry);
        }
    }

    public void clearRetryInfo(String uploadId) {
        retryMap.remove(uploadId);
    }
}