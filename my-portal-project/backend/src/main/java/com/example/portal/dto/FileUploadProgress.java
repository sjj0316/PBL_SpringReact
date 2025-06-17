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
    private FileUploadStatus status;

    public static FileUploadProgress of(String uploadId, String fileName, long totalBytes) {
        return FileUploadProgress.builder()
                .uploadId(uploadId)
                .fileName(fileName)
                .totalBytes(totalBytes)
                .uploadedBytes(0)
                .startTime(LocalDateTime.now())
                .lastUpdateTime(LocalDateTime.now())
                .completed(false)
                .status(FileUploadStatus.PENDING)
                .build();
    }

    public void updateProgress(long uploadedBytes) {
        this.uploadedBytes = uploadedBytes;
        this.lastUpdateTime = LocalDateTime.now();
        this.status = FileUploadStatus.IN_PROGRESS;
    }

    public void complete() {
        this.completed = true;
        this.lastUpdateTime = LocalDateTime.now();
        this.status = FileUploadStatus.COMPLETED;
    }

    public void fail(String errorMessage) {
        this.completed = true;
        this.errorMessage = errorMessage;
        this.lastUpdateTime = LocalDateTime.now();
        this.status = FileUploadStatus.FAILED;
    }

    public void cancel() {
        this.completed = true;
        this.lastUpdateTime = LocalDateTime.now();
        this.status = FileUploadStatus.CANCELLED;
    }

    public int getProgressPercentage() {
        if (totalBytes == 0)
            return 0;
        return (int) ((uploadedBytes * 100) / totalBytes);
    }
}