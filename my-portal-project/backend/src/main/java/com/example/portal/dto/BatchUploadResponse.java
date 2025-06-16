package com.example.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchUploadResponse {
    private String batchId;
    private List<String> successfulUploads;
    private List<String> failedUploads;
    private int totalFiles;
    private int successCount;
    private int failureCount;
    private String status;
    private String errorMessage;
}