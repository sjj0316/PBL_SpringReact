package com.example.portal.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileValidation {
    private String validationId;
    private String fileId;
    private ValidationStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<ValidationResult> results;
    private String errorMessage;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ValidationResult {
        private String checkName;
        private boolean passed;
        private String message;
        private LocalDateTime checkTime;
    }

    public static FileValidation of(String fileId) {
        return FileValidation.builder()
                .validationId(java.util.UUID.randomUUID().toString())
                .fileId(fileId)
                .status(ValidationStatus.PENDING)
                .startTime(LocalDateTime.now())
                .results(new ArrayList<>())
                .build();
    }

    public static FileValidation of(String fileId, String fileName, long fileSize, String fileType) {
        return FileValidation.builder()
                .validationId(java.util.UUID.randomUUID().toString())
                .fileId(fileId)
                .status(ValidationStatus.PENDING)
                .startTime(LocalDateTime.now())
                .results(new ArrayList<>())
                .build();
    }

    public void addResult(ValidationResult result) {
        if (results == null) {
            results = new ArrayList<>();
        }
        results.add(result);
    }

    public void addValidationResult(String checkName, boolean passed, String message) {
        ValidationResult result = ValidationResult.builder()
                .checkName(checkName)
                .passed(passed)
                .message(message)
                .checkTime(LocalDateTime.now())
                .build();
        addResult(result);
    }

    public void startValidation() {
        this.status = ValidationStatus.VALIDATING;
        this.startTime = LocalDateTime.now();
    }

    public void completeValidation() {
        this.status = ValidationStatus.VALID;
        this.endTime = LocalDateTime.now();
    }

    public void failValidation(String errorMessage) {
        this.status = ValidationStatus.INVALID;
        this.errorMessage = errorMessage;
        this.endTime = LocalDateTime.now();
    }

    public boolean isSafe() {
        return status == ValidationStatus.VALID;
    }
}