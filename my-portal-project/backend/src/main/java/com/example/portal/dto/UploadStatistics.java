package com.example.portal.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadStatistics {
    private String uploadId;
    private String fileName;
    private long fileSize;
    private long uploadedBytes;
    private LocalDateTime startTime;
    private LocalDateTime lastUpdateTime;
    private LocalDateTime endTime;
    private double currentSpeed;
    private double averageSpeed;
    private int retryCount;
    private int errorCount;
    private boolean completed;
    private boolean error;
    private String errorMessage;
    private String status;

    public static UploadStatistics of(String uploadId, String fileName, long fileSize) {
        LocalDateTime now = LocalDateTime.now();
        return UploadStatistics.builder()
                .uploadId(uploadId)
                .fileName(fileName)
                .fileSize(fileSize)
                .uploadedBytes(0)
                .startTime(now)
                .lastUpdateTime(now)
                .currentSpeed(0)
                .averageSpeed(0)
                .retryCount(0)
                .errorCount(0)
                .completed(false)
                .error(false)
                .status("PENDING")
                .build();
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public void incrementErrorCount() {
        this.errorCount++;
    }

    public String getStatus() {
        if (error)
            return "ERROR";
        if (completed)
            return "COMPLETED";
        if (retryCount > 0)
            return "RETRYING";
        return "IN_PROGRESS";
    }

    public int getProgressPercentage() {
        if (fileSize == 0)
            return 0;
        return (int) ((uploadedBytes * 100) / fileSize);
    }

    public void updateProgress(long uploadedBytes) {
        this.uploadedBytes = uploadedBytes;
        this.lastUpdateTime = LocalDateTime.now();
    }

    public void complete() {
        this.completed = true;
        this.endTime = LocalDateTime.now();
        this.lastUpdateTime = this.endTime;
    }

    public void fail(String errorMessage) {
        this.error = true;
        this.errorMessage = errorMessage;
        this.endTime = LocalDateTime.now();
        this.lastUpdateTime = this.endTime;
    }

    public void setUploadedBytes(long uploadedBytes) {
        this.uploadedBytes = uploadedBytes;
    }

    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setCurrentSpeed(double currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public long getAverageSpeed() {
        return (long) averageSpeed;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public int getRetryCount() {
        return retryCount;
    }
}