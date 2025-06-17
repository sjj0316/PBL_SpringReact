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
public class UploadOptimization {
    private String optimizationId;
    private String uploadId;
    @Builder.Default
    private int chunkSize = 1024 * 1024; // 기본 1MB
    @Builder.Default
    private int maxConcurrentUploads = 3;
    @Builder.Default
    private long memoryLimit = 100 * 1024 * 1024; // 기본 100MB
    @Builder.Default
    private List<OptimizationMetric> metrics = new ArrayList<>();
    private String strategy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String fileId;
    private long originalSize;
    private long optimizedSize;
    private String optimizationType;
    private LocalDateTime optimizationTime;
    private boolean success;
    private String errorMessage;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptimizationMetric {
        private String name;
        private double value;
        private String unit;
        private LocalDateTime timestamp;
    }

    public static UploadOptimization of(String fileId, long originalSize, long optimizedSize, String optimizationType) {
        return UploadOptimization.builder()
                .fileId(fileId)
                .originalSize(originalSize)
                .optimizedSize(optimizedSize)
                .optimizationType(optimizationType)
                .optimizationTime(LocalDateTime.now())
                .success(true)
                .build();
    }

    public static UploadOptimization of(String uploadId) {
        return UploadOptimization.builder()
                .uploadId(uploadId)
                .optimizationTime(LocalDateTime.now())
                .success(false)
                .build();
    }

    public static UploadOptimization failed(String fileId, String errorMessage) {
        return UploadOptimization.builder()
                .fileId(fileId)
                .optimizationTime(LocalDateTime.now())
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }

    public void addMetric(String name, double value, String unit) {
        if (metrics == null) {
            metrics = new ArrayList<>();
        }
        metrics.add(new OptimizationMetric(name, value, unit, LocalDateTime.now()));
    }

    public void optimizeChunkSize(long fileSize) {
        // 파일 크기에 따라 청크 크기 최적화
        if (fileSize < 10 * 1024 * 1024) { // 10MB 미만
            this.chunkSize = 512 * 1024; // 512KB
        } else if (fileSize < 100 * 1024 * 1024) { // 100MB 미만
            this.chunkSize = 1024 * 1024; // 1MB
        } else {
            this.chunkSize = 2 * 1024 * 1024; // 2MB
        }
    }

    public void optimizeConcurrentUploads(int availableCores) {
        // 시스템 리소스에 따라 동시 업로드 수 최적화
        this.maxConcurrentUploads = Math.min(availableCores, 5);
    }

    public void optimizeMemoryLimit(long totalMemory) {
        // 시스템 메모리에 따라 메모리 제한 최적화
        this.memoryLimit = Math.min(totalMemory / 4, 500 * 1024 * 1024); // 최대 500MB
    }

    public void setOptimizationStrategy(String strategy) {
        this.strategy = strategy;
    }

    public double getAverageMetricValue(String metricName) {
        if (metrics == null) {
            return 0.0;
        }
        return metrics.stream()
                .filter(m -> m.getName().equals(metricName))
                .mapToDouble(OptimizationMetric::getValue)
                .average()
                .orElse(0.0);
    }

    public List<OptimizationMetric> getLatestMetrics(int count) {
        if (metrics == null) {
            return new ArrayList<>();
        }
        return metrics.stream()
                .sorted((m1, m2) -> m2.getTimestamp().compareTo(m1.getTimestamp()))
                .limit(count)
                .toList();
    }

    public String getOptimizationStrategy() {
        return strategy;
    }
}