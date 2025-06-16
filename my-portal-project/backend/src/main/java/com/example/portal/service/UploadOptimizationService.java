package com.example.portal.service;

import com.example.portal.dto.UploadOptimization;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UploadOptimizationService {
    private final Map<String, UploadOptimization> optimizationMap = new ConcurrentHashMap<>();
    private final UploadStatisticsService statisticsService;

    public UploadOptimization optimizeUpload(String uploadId, MultipartFile file) {
        UploadOptimization optimization = UploadOptimization.of(uploadId);

        try {
            // 파일 크기에 따른 청크 크기 최적화
            optimization.optimizeChunkSize(file.getSize());

            // 시스템 리소스에 따른 동시 업로드 수 최적화
            int availableCores = Runtime.getRuntime().availableProcessors();
            optimization.optimizeConcurrentUploads(availableCores);

            // 시스템 메모리에 따른 메모리 제한 최적화
            long totalMemory = Runtime.getRuntime().totalMemory();
            optimization.optimizeMemoryLimit(totalMemory);

            // 성능 메트릭 수집
            collectPerformanceMetrics(optimization, file);

            // 최적화 전략 설정
            setOptimizationStrategy(optimization, file);

            optimizationMap.put(optimization.getOptimizationId(), optimization);

        } catch (Exception e) {
            optimization.setOptimizationStrategy("DEFAULT");
        }

        return optimization;
    }

    private void collectPerformanceMetrics(UploadOptimization optimization, MultipartFile file) {
        // 업로드 속도 메트릭
        double uploadSpeed = calculateUploadSpeed(file);
        optimization.addMetric("UPLOAD_SPEED", uploadSpeed, "bytes/sec");

        // 메모리 사용량 메트릭
        double memoryUsage = calculateMemoryUsage();
        optimization.addMetric("MEMORY_USAGE", memoryUsage, "bytes");

        // CPU 사용량 메트릭
        double cpuUsage = calculateCpuUsage();
        optimization.addMetric("CPU_USAGE", cpuUsage, "%");
    }

    private double calculateUploadSpeed(MultipartFile file) {
        // 실제 구현에서는 실제 업로드 속도 측정
        return file.getSize() / 1024.0; // 임시 구현
    }

    private double calculateMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        return usedMemory;
    }

    private double calculateCpuUsage() {
        // 실제 구현에서는 실제 CPU 사용량 측정
        return 0.0; // 임시 구현
    }

    private void setOptimizationStrategy(UploadOptimization optimization, MultipartFile file) {
        long fileSize = file.getSize();
        String strategy;

        if (fileSize < 5 * 1024 * 1024) { // 5MB 미만
            strategy = "DIRECT_UPLOAD";
        } else if (fileSize < 50 * 1024 * 1024) { // 50MB 미만
            strategy = "CHUNKED_UPLOAD";
        } else {
            strategy = "STREAMING_UPLOAD";
        }

        optimization.setOptimizationStrategy(strategy);
    }

    public UploadOptimization getOptimization(String optimizationId) {
        return optimizationMap.get(optimizationId);
    }

    public List<UploadOptimization> getOptimizationsByStrategy(String strategy) {
        return optimizationMap.values().stream()
                .filter(o -> o.getOptimizationStrategy().equals(strategy))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getOptimizationMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        List<UploadOptimization> allOptimizations = new ArrayList<>(optimizationMap.values());

        metrics.put("totalOptimizations", allOptimizations.size());

        // 전략별 최적화 수
        Map<String, Long> strategyCounts = allOptimizations.stream()
                .collect(Collectors.groupingBy(
                        UploadOptimization::getOptimizationStrategy,
                        Collectors.counting()));
        metrics.put("strategyCounts", strategyCounts);

        // 평균 업로드 속도
        double avgSpeed = allOptimizations.stream()
                .mapToDouble(o -> o.getAverageMetricValue("UPLOAD_SPEED"))
                .average()
                .orElse(0.0);
        metrics.put("averageUploadSpeed", avgSpeed);

        // 평균 메모리 사용량
        double avgMemory = allOptimizations.stream()
                .mapToDouble(o -> o.getAverageMetricValue("MEMORY_USAGE"))
                .average()
                .orElse(0.0);
        metrics.put("averageMemoryUsage", avgMemory);

        return metrics;
    }

    public void clearOptimizations() {
        optimizationMap.clear();
    }
}