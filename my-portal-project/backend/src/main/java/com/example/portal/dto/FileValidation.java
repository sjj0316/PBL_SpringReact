package com.example.portal.dto;

import com.example.portal.enums.ValidationStatus;
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

    public void addResult(ValidationResult result) {
        if (results == null) {
            results = new ArrayList<>();
        }
        results.add(result);
    }

    public boolean isSafe() {
        return status == ValidationStatus.VALID;
    }
}