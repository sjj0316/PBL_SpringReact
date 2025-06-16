package com.example.portal.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadProgress {
    private String uploadId;
    private String fileName;
    private long totalBytes;
    private long uploadedBytes;
    private LocalDateTime startTime;
    private LocalDateTime lastUpdateTime;
    private boolean completed;
    private String errorMessage;

    public static FileUploadProgress of(String uploadId, String fileName, long totalBytes) {
        return FileUploadProgress.builder()
                .uploadId(uploadId)
                .fileName(fileName)
                .totalBytes(totalBytes)
                .uploadedBytes(0)
                .startTime(LocalDateTime.now())
                .lastUpdateTime(LocalDateTime.now())
                .completed(false)
                .build();
    }

    public void updateProgress(long uploadedBytes) {
        this.uploadedBytes = uploadedBytes;
        this.lastUpdateTime = LocalDateTime.now();
    }

    public void complete() {
        this.completed = true;
        this.lastUpdateTime = LocalDateTime.now();
    }

    public void fail(String errorMessage) {
        this.completed = true;
        this.errorMessage = errorMessage;
        this.lastUpdateTime = LocalDateTime.now();
    }

    public int getProgressPercentage() {
        if (totalBytes == 0)
            return 0;
        return (int) ((uploadedBytes * 100) / totalBytes);
    }
}