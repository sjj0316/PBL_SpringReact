package com.example.portal.service;

import com.example.portal.dto.UploadStatistics;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UploadStatisticsService {
    private final Map<String, UploadStatistics> statisticsMap = new ConcurrentHashMap<>();
    private final FileUploadProgressService progressService;
    private final FileUploadRetryService retryService;

    public UploadStatistics initializeStatistics(String uploadId) {
        UploadStatistics stats = UploadStatistics.of(uploadId, "unknown", 0);
        statisticsMap.put(uploadId, stats);
        return stats;
    }

    public UploadStatistics getStatistics(String uploadId) {
        return statisticsMap.get(uploadId);
    }

    public void updateProgress(String uploadId, long uploadedBytes, long totalBytes) {
        UploadStatistics stats = statisticsMap.get(uploadId);
        if (stats != null) {
            stats.updateProgress(uploadedBytes, totalBytes);
        }
    }

    public void completeUpload(String uploadId) {
        UploadStatistics stats = statisticsMap.get(uploadId);
        if (stats != null) {
            stats.complete();
        }
    }

    public void failUpload(String uploadId, String error) {
        UploadStatistics stats = statisticsMap.get(uploadId);
        if (stats != null) {
            stats.fail(error);
        }
    }

    public void incrementRetryCount(String uploadId) {
        UploadStatistics stats = statisticsMap.get(uploadId);
        if (stats != null) {
            stats.incrementRetryCount();
        }
    }

    public void incrementErrorCount(String uploadId) {
        UploadStatistics stats = statisticsMap.get(uploadId);
        if (stats != null) {
            stats.incrementErrorCount();
        }
    }

    @Scheduled(fixedRate = 5000)
    public void cleanupOldStatistics() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        statisticsMap.entrySet().removeIf(entry -> {
            UploadStatistics stats = entry.getValue();
            return stats.getEndTime() != null &&
                    stats.getEndTime().isBefore(threshold);
        });
    }

    public Map<String, Object> getSystemMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        // 전체 업로드 통계
        List<UploadStatistics> allStats = new ArrayList<>(statisticsMap.values());
        metrics.put("totalUploads", allStats.size());

        // 상태별 통계
        Map<String, Long> statusCounts = allStats.stream()
                .collect(Collectors.groupingBy(
                        UploadStatistics::getStatus,
                        Collectors.counting()));
        metrics.put("statusCounts", statusCounts);

        // 평균 속도 계산
        double avgSpeed = allStats.stream()
                .mapToDouble(UploadStatistics::getAverageSpeed)
                .average()
                .orElse(0.0);
        metrics.put("averageSpeed", avgSpeed);

        // 에러율 계산
        long totalErrors = allStats.stream()
                .mapToInt(UploadStatistics::getErrorCount)
                .sum();
        double errorRate = allStats.isEmpty() ? 0.0 : (double) totalErrors / allStats.size() * 100;
        metrics.put("errorRate", errorRate);

        // 재시도율 계산
        long totalRetries = allStats.stream()
                .mapToInt(UploadStatistics::getRetryCount)
                .sum();
        double retryRate = allStats.isEmpty() ? 0.0 : (double) totalRetries / allStats.size() * 100;
        metrics.put("retryRate", retryRate);

        return metrics;
    }

    public List<UploadStatistics> getActiveUploads() {
        return statisticsMap.values().stream()
                .filter(stats -> "IN_PROGRESS".equals(stats.getStatus()))
                .collect(Collectors.toList());
    }

    public List<UploadStatistics> getFailedUploads() {
        return statisticsMap.values().stream()
                .filter(stats -> "FAILED".equals(stats.getStatus()))
                .collect(Collectors.toList());
    }

    public void clearStatistics() {
        statisticsMap.clear();
    }

    public void recordUpload(String uploadId, long fileSize) {
        UploadStatistics stats = UploadStatistics.builder()
                .uploadId(uploadId)
                .fileSize(fileSize)
                .startTime(LocalDateTime.now())
                .build();
        statisticsMap.put(uploadId, stats);
    }

    public void recordProgress(String uploadId, long uploadedBytes) {
        UploadStatistics stats = statisticsMap.get(uploadId);
        if (stats != null) {
            stats.setUploadedBytes(uploadedBytes);
            stats.setLastUpdateTime(LocalDateTime.now());
        }
    }

    public void recordCompletion(String uploadId) {
        UploadStatistics stats = statisticsMap.get(uploadId);
        if (stats != null) {
            stats.setCompleted(true);
            stats.setEndTime(LocalDateTime.now());
        }
    }

    public void recordError(String uploadId, String errorMessage) {
        UploadStatistics stats = statisticsMap.get(uploadId);
        if (stats != null) {
            stats.setError(true);
            stats.setErrorMessage(errorMessage);
            stats.setEndTime(LocalDateTime.now());
        }
    }

    public void recordSpeed(String uploadId, long bytesPerSecond) {
        UploadStatistics stats = statisticsMap.get(uploadId);
        if (stats != null) {
            stats.setCurrentSpeed(bytesPerSecond);
            if (stats.getAverageSpeed() == 0) {
                stats.setAverageSpeed(bytesPerSecond);
            } else {
                stats.setAverageSpeed((stats.getAverageSpeed() + bytesPerSecond) / 2);
            }
        }
    }

    public List<UploadStatistics> getAllStatistics() {
        return List.copyOf(statisticsMap.values());
    }

    public void removeStatistics(String uploadId) {
        statisticsMap.remove(uploadId);
    }
}