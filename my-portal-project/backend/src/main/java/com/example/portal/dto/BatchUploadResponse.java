package com.example.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchUploadResponse {
    private String batchId;
    private List<String> successfulUploads;
    private List<String> failedUploads;
    private List<String> failedFileNames;
    private int totalFiles;
    private int successCount;
    private int failureCount;
    private double progress;
    private String status;
    private String errorMessage;
    private Map<String, FileUploadStatus> fileStatuses;

    public static BatchUploadResponse of(String batchId, int totalFiles) {
        return BatchUploadResponse.builder()
                .batchId(batchId)
                .totalFiles(totalFiles)
                .successCount(0)
                .failureCount(0)
                .progress(0.0)
                .status("PENDING")
                .fileStatuses(new HashMap<>())
                .build();
    }

    public void updateProgress(int completed, int failed, double progress) {
        this.successCount = completed;
        this.failureCount = failed;
        this.progress = progress;
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public void updateError(String errorMessage) {
        this.errorMessage = errorMessage;
        this.status = "FAILED";
    }
}