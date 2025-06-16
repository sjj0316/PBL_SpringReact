package com.example.portal.service;

import com.example.portal.dto.UploadRecovery;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UploadRecoveryService {
    private final Map<String, UploadRecovery> recoveryMap = new ConcurrentHashMap<>();
    private final FileUploadProgressService progressService;
    private final FileUploadRetryService retryService;
    private final UploadStatisticsService statisticsService;

    public UploadRecovery initializeRecovery(String uploadId, String originalFileName,
            long fileSize, String errorType, String errorMessage) {
        UploadRecovery recovery = UploadRecovery.of(uploadId, originalFileName, fileSize,
                errorType, errorMessage);
        recoveryMap.put(recovery.getRecoveryId(), recovery);
        return recovery;
    }

    public UploadRecovery getRecovery(String recoveryId) {
        return recoveryMap.get(recoveryId);
    }

    public List<UploadRecovery> getRecoveriesByStatus(UploadRecovery.RecoveryStatus status) {
        return recoveryMap.values().stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }

    public void attemptRecovery(String recoveryId, String strategy) {
        UploadRecovery recovery = recoveryMap.get(recoveryId);
        if (recovery != null && recovery.canAttemptRecovery()) {
            recovery.startRecovery();
            try {
                boolean success = executeRecoveryStrategy(recovery, strategy);
                recovery.addRecoveryAttempt(strategy, success,
                        success ? "Recovery successful" : "Recovery failed");

                if (success) {
                    recovery.completeRecovery();
                    statisticsService.incrementRecoveryCount(recovery.getUploadId());
                } else if (!recovery.canAttemptRecovery()) {
                    recovery.failRecovery("Max recovery attempts exceeded");
                }
            } catch (Exception e) {
                recovery.addRecoveryAttempt(strategy, false, e.getMessage());
                if (!recovery.canAttemptRecovery()) {
                    recovery.failRecovery("Recovery failed: " + e.getMessage());
                }
            }
        }
    }

    private boolean executeRecoveryStrategy(UploadRecovery recovery, String strategy) {
        switch (strategy) {
            case "RETRY":
                return retryService.canRetry(recovery.getUploadId());
            case "RESUME":
                return progressService.canResume(recovery.getUploadId());
            case "RESTART":
                return progressService.canRestart(recovery.getUploadId());
            default:
                throw new IllegalArgumentException("Unknown recovery strategy: " + strategy);
        }
    }

    @Scheduled(fixedRate = 300000) // 5분마다 실행
    public void cleanupOldRecoveries() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(7);
        recoveryMap.entrySet().removeIf(entry -> {
            UploadRecovery recovery = entry.getValue();
            return recovery.getStatus() == UploadRecovery.RecoveryStatus.COMPLETED &&
                    recovery.getRecoveryTime().isBefore(threshold);
        });
    }

    public List<UploadRecovery> getPendingRecoveries() {
        return getRecoveriesByStatus(UploadRecovery.RecoveryStatus.PENDING);
    }

    public List<UploadRecovery> getFailedRecoveries() {
        return getRecoveriesByStatus(UploadRecovery.RecoveryStatus.FAILED);
    }

    public void clearRecoveries() {
        recoveryMap.clear();
    }

    public Map<String, Object> getRecoveryMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        List<UploadRecovery> allRecoveries = new ArrayList<>(recoveryMap.values());

        metrics.put("totalRecoveries", allRecoveries.size());

        Map<UploadRecovery.RecoveryStatus, Long> statusCounts = allRecoveries.stream()
                .collect(Collectors.groupingBy(
                        UploadRecovery::getStatus,
                        Collectors.counting()));
        metrics.put("statusCounts", statusCounts);

        double successRate = allRecoveries.stream()
                .filter(r -> r.getStatus() == UploadRecovery.RecoveryStatus.COMPLETED)
                .count() / (double) allRecoveries.size() * 100;
        metrics.put("successRate", successRate);

        return metrics;
    }
}