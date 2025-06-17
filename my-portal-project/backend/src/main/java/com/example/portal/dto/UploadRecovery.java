package com.example.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadRecovery {
    private String recoveryId;
    private String uploadId;
    private RecoveryStatus status;
    private LocalDateTime recoveryTime;
    private int retryCount;
    private String errorMessage;
    private String recoveryType;
    private boolean success;
    private List<RecoveryAttempt> recoveryAttempts;
    private int maxRetryCount;

    public enum RecoveryStatus {
        PENDING,
        IN_PROGRESS,
        SUCCESS,
        FAILED
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecoveryAttempt {
        private String attemptId;
        private String strategy;
        private boolean success;
        private String message;
        private LocalDateTime attemptTime;
    }

    public static UploadRecovery of(String uploadId, String originalFileName, long fileSize, String errorType,
            String errorMessage) {
        return UploadRecovery.builder()
                .recoveryId(java.util.UUID.randomUUID().toString())
                .uploadId(uploadId)
                .status(RecoveryStatus.PENDING)
                .recoveryTime(LocalDateTime.now())
                .retryCount(0)
                .errorMessage(errorMessage)
                .recoveryType(errorType)
                .success(false)
                .recoveryAttempts(new ArrayList<>())
                .maxRetryCount(3)
                .build();
    }

    public boolean canAttemptRecovery() {
        return retryCount < maxRetryCount && status != RecoveryStatus.SUCCESS;
    }

    public void startRecovery() {
        this.status = RecoveryStatus.IN_PROGRESS;
        this.recoveryTime = LocalDateTime.now();
    }

    public void addRecoveryAttempt(String strategy, boolean success, String message) {
        if (recoveryAttempts == null) {
            recoveryAttempts = new ArrayList<>();
        }

        RecoveryAttempt attempt = RecoveryAttempt.builder()
                .attemptId(java.util.UUID.randomUUID().toString())
                .strategy(strategy)
                .success(success)
                .message(message)
                .attemptTime(LocalDateTime.now())
                .build();

        recoveryAttempts.add(attempt);
        retryCount++;
    }

    public void completeRecovery() {
        this.status = RecoveryStatus.SUCCESS;
        this.success = true;
    }

    public void failRecovery(String errorMessage) {
        this.status = RecoveryStatus.FAILED;
        this.success = false;
        this.errorMessage = errorMessage;
    }
}