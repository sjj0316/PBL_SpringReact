package com.example.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadRecovery {
    private String recoveryId;
    private String uploadId;
    private String status;
    private LocalDateTime recoveryTime;
    private int retryCount;
    private String errorMessage;
    private String recoveryType;
    private boolean success;
}