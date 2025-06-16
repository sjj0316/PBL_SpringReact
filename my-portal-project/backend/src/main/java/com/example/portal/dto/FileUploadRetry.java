package com.example.portal.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileUploadRetry {
    private String uploadId;
    private int retryCount;
    private long lastRetryTime;
    private String errorMessage;
    private long bytesUploaded;
    private boolean isRetrying;

    public static FileUploadRetry of(String uploadId) {
        return FileUploadRetry.builder()
                .uploadId(uploadId)
                .retryCount(0)
                .lastRetryTime(System.currentTimeMillis())
                .isRetrying(false)
                .build();
    }

    public FileUploadRetry incrementRetryCount(String errorMessage) {
        return FileUploadRetry.builder()
                .uploadId(this.uploadId)
                .retryCount(this.retryCount + 1)
                .lastRetryTime(System.currentTimeMillis())
                .errorMessage(errorMessage)
                .bytesUploaded(this.bytesUploaded)
                .isRetrying(true)
                .build();
    }

    public FileUploadRetry updateProgress(long bytesUploaded) {
        return FileUploadRetry.builder()
                .uploadId(this.uploadId)
                .retryCount(this.retryCount)
                .lastRetryTime(this.lastRetryTime)
                .errorMessage(this.errorMessage)
                .bytesUploaded(bytesUploaded)
                .isRetrying(this.isRetrying)
                .build();
    }
}