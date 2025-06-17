package com.example.portal.controller;

import com.example.portal.dto.BatchUploadRequest;
import com.example.portal.dto.BatchUploadResponse;
import com.example.portal.dto.FileUploadPause;
import com.example.portal.dto.FileUploadProgress;
import com.example.portal.dto.FileUploadRetry;
import com.example.portal.dto.FileUploadStatus;
import com.example.portal.dto.UploadPriority;
import com.example.portal.dto.UploadSchedule;
import com.example.portal.dto.UploadStatistics;
import com.example.portal.dto.UploadRecovery;
import com.example.portal.dto.FileValidation;
import com.example.portal.dto.UploadOptimization;
import com.example.portal.dto.ValidationStatus;
import com.example.portal.entity.PostFile;
import com.example.portal.service.BatchUploadService;
import com.example.portal.service.FileStorageService;
import com.example.portal.service.FileUploadPauseService;
import com.example.portal.service.FileUploadProgressService;
import com.example.portal.service.FileUploadRetryService;
import com.example.portal.service.PostFileService;
import com.example.portal.service.UploadPriorityService;
import com.example.portal.service.UploadRateLimitService;
import com.example.portal.service.UploadScheduleService;
import com.example.portal.service.UploadStatisticsService;
import com.example.portal.service.UploadRecoveryService;
import com.example.portal.service.FileValidationService;
import com.example.portal.service.UploadOptimizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class PostFileController {

    private final PostFileService postFileService;
    private final FileStorageService fileStorageService;
    private final FileUploadProgressService progressService;
    private final FileUploadRetryService retryService;
    private final FileUploadPauseService pauseService;
    private final BatchUploadService batchUploadService;
    private final UploadRateLimitService rateLimitService;
    private final UploadPriorityService priorityService;
    private final UploadScheduleService scheduleService;
    private final UploadStatisticsService statisticsService;
    private final UploadRecoveryService recoveryService;
    private final FileValidationService validationService;
    private final UploadOptimizationService optimizationService;

    @PostMapping("/{postId}")
    public ResponseEntity<PostFile> uploadFile(
            @PathVariable Long postId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "uploadId", required = false) String uploadId,
            @RequestParam(value = "rateLimit", required = false) Integer rateLimit,
            @RequestParam(value = "priority", required = false) Integer priority) {

        if (uploadId != null) {
            if (progressService.isCancelled(uploadId)) {
                return ResponseEntity.badRequest().build();
            }

            if (pauseService.isPaused(uploadId)) {
                return ResponseEntity.accepted().build();
            }

            FileUploadRetry retry = retryService.getRetryInfo(uploadId);
            if (retry != null && retry.isRetrying()) {
                return ResponseEntity.accepted().build();
            }

            // 속도 제한 초기화
            if (rateLimit != null) {
                rateLimitService.initializeRateLimiter(uploadId, rateLimit);
            }

            // 우선순위 설정
            if (priority != null) {
                priorityService.setPriority(UploadPriority.userDefined(uploadId, priority));
            } else {
                priorityService.setPriority(priorityService.calculatePriority(uploadId, file));
            }
        }

        try {
            PostFile savedFile = postFileService.saveFile(file, postId);
            if (uploadId != null) {
                progressService.removeProgress(uploadId);
                retryService.clearRetryInfo(uploadId);
                pauseService.clearPauseInfo(uploadId);
                rateLimitService.removeRateLimiter(uploadId);
                priorityService.removePriority(uploadId);
            }
            return ResponseEntity.ok(savedFile);
        } catch (Exception e) {
            if (uploadId != null) {
                retryService.handleUploadError(uploadId, e.getMessage());
            }
            throw e;
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<PostFile>> getFilesByPostId(@PathVariable Long postId) {
        List<PostFile> files = postFileService.getFilesByPostId(postId);
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/post/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) {
        postFileService.deleteFile(fileId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/post/{fileId}")
    public ResponseEntity<PostFile> getFileById(@PathVariable Long fileId) {
        PostFile file = postFileService.getFileById(fileId);
        return ResponseEntity.ok(file);
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            PostFile postFile = postFileService.getFileById(Long.parseLong(fileName));
            Resource resource = fileStorageService.loadFileAsResource(postFile.getStoredName());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(postFile.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + postFile.getOriginalName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/preview/{fileName:.+}")
    public ResponseEntity<Resource> previewFile(@PathVariable String fileName) {
        try {
            PostFile postFile = postFileService.getFileById(Long.parseLong(fileName));
            Resource resource = fileStorageService.loadFileAsResource(postFile.getStoredName());

            if (postFile.getFileType().startsWith("image/")) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(postFile.getFileType()))
                        .body(resource);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/progress/{uploadId}")
    public ResponseEntity<FileUploadProgress> getUploadProgress(@PathVariable String uploadId) {
        FileUploadProgress progress = progressService.getProgress(uploadId);
        if (progress == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(progress);
    }

    @PostMapping("/cancel/{uploadId}")
    public ResponseEntity<Void> cancelUpload(@PathVariable String uploadId) {
        progressService.cancelUpload(uploadId);
        retryService.clearRetryInfo(uploadId);
        pauseService.clearPauseInfo(uploadId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/retry/{uploadId}")
    public ResponseEntity<FileUploadRetry> getRetryInfo(@PathVariable String uploadId) {
        FileUploadRetry retry = retryService.getRetryInfo(uploadId);
        if (retry == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(retry);
    }

    @PostMapping("/pause/{uploadId}")
    public ResponseEntity<FileUploadPause> pauseUpload(
            @PathVariable String uploadId,
            @RequestParam(required = false) String reason) {
        FileUploadPause pause = pauseService.pauseUpload(uploadId, reason);
        if (pause == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pause);
    }

    @PostMapping("/resume/{uploadId}")
    public ResponseEntity<FileUploadPause> resumeUpload(@PathVariable String uploadId) {
        FileUploadPause pause = pauseService.resumeUpload(uploadId);
        if (pause == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pause);
    }

    @GetMapping("/pause/{uploadId}")
    public ResponseEntity<FileUploadPause> getPauseInfo(@PathVariable String uploadId) {
        FileUploadPause pause = pauseService.getPauseInfo(uploadId);
        if (pause == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pause);
    }

    @PostMapping("/batch/{postId}")
    public ResponseEntity<BatchUploadResponse> batchUpload(
            @PathVariable Long postId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "maxConcurrentUploads", defaultValue = "3") int maxConcurrentUploads,
            @RequestParam(value = "compressImages", defaultValue = "true") boolean compressImages,
            @RequestParam(value = "uploadStrategy", defaultValue = "PARALLEL") String uploadStrategy) {

        BatchUploadRequest request = new BatchUploadRequest();
        request.setPostId(postId);
        request.setMaxConcurrentUploads(maxConcurrentUploads);
        request.setCompressImages(compressImages);
        request.setUploadStrategy(uploadStrategy);

        BatchUploadResponse response = batchUploadService.startBatchUpload(request, files);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/batch/{batchId}")
    public ResponseEntity<BatchUploadResponse> getBatchStatus(@PathVariable String batchId) {
        BatchUploadResponse response = batchUploadService.getBatchStatus(batchId);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/batch/{batchId}/cancel")
    public ResponseEntity<Void> cancelBatchUpload(@PathVariable String batchId) {
        batchUploadService.cancelBatchUpload(batchId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rate-limit/{uploadId}")
    public ResponseEntity<Void> updateRateLimit(
            @PathVariable String uploadId,
            @RequestParam int rateLimit) {
        rateLimitService.updateRateLimit(uploadId, rateLimit);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rate-limit/{uploadId}")
    public ResponseEntity<Integer> getRateLimit(@PathVariable String uploadId) {
        int rateLimit = rateLimitService.getCurrentRateLimit(uploadId);
        return ResponseEntity.ok(rateLimit);
    }

    @PostMapping("/priority/{uploadId}")
    public ResponseEntity<UploadPriority> setPriority(
            @PathVariable String uploadId,
            @RequestParam int priority) {
        priorityService.updatePriority(uploadId, priority);
        UploadPriority updatedPriority = priorityService.getPriority(uploadId);
        return ResponseEntity.ok(updatedPriority);
    }

    @GetMapping("/priority/{uploadId}")
    public ResponseEntity<UploadPriority> getPriority(@PathVariable String uploadId) {
        UploadPriority priority = priorityService.getPriority(uploadId);
        if (priority == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(priority);
    }

    @GetMapping("/priorities")
    public ResponseEntity<List<UploadPriority>> getPrioritizedUploads() {
        List<UploadPriority> priorities = priorityService.getPrioritizedUploads();
        return ResponseEntity.ok(priorities);
    }

    @GetMapping("/priorities/{type}")
    public ResponseEntity<List<UploadPriority>> getPrioritiesByType(
            @PathVariable UploadPriority.PriorityType type) {
        List<UploadPriority> priorities = priorityService.getPrioritiesByType(type);
        return ResponseEntity.ok(priorities);
    }

    @PostMapping("/schedule")
    public ResponseEntity<UploadSchedule> scheduleUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("postId") Long postId,
            @RequestParam("scheduledTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledTime,
            @RequestParam(value = "type", defaultValue = "TIME_BASED") UploadSchedule.ScheduleType type,
            @RequestParam(value = "conditions", required = false) Map<String, Object> conditions) {

        String uploadId = UUID.randomUUID().toString();
        UploadSchedule schedule = UploadSchedule.of(uploadId, scheduledTime, type);
        if (conditions != null) {
            schedule.setConditions(conditions);
        }

        return ResponseEntity.ok(scheduleService.scheduleUpload(schedule));
    }

    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<UploadSchedule> getSchedule(@PathVariable String scheduleId) {
        UploadSchedule schedule = scheduleService.getSchedule(scheduleId);
        if (schedule == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(schedule);
    }

    @DeleteMapping("/schedule/{scheduleId}")
    public ResponseEntity<Void> cancelSchedule(@PathVariable String scheduleId) {
        scheduleService.cancelSchedule(scheduleId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/schedules")
    public ResponseEntity<List<UploadSchedule>> getSchedules(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "type", required = false) UploadSchedule.ScheduleType type) {

        if (status != null) {
            return ResponseEntity.ok(scheduleService.getSchedulesByStatus(status));
        } else if (type != null) {
            return ResponseEntity.ok(scheduleService.getSchedulesByType(type));
        } else {
            return ResponseEntity.ok(scheduleService.getUpcomingSchedules());
        }
    }

    @GetMapping("/statistics/{uploadId}")
    public ResponseEntity<UploadStatistics> getUploadStatistics(@PathVariable String uploadId) {
        UploadStatistics stats = statisticsService.getStatistics(uploadId);
        if (stats == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/statistics/system")
    public ResponseEntity<Map<String, Object>> getSystemStatistics() {
        return ResponseEntity.ok(statisticsService.getSystemMetrics());
    }

    @GetMapping("/statistics/active")
    public ResponseEntity<List<UploadStatistics>> getActiveUploads() {
        return ResponseEntity.ok(statisticsService.getActiveUploads());
    }

    @GetMapping("/statistics/failed")
    public ResponseEntity<List<UploadStatistics>> getFailedUploads() {
        return ResponseEntity.ok(statisticsService.getFailedUploads());
    }

    @DeleteMapping("/statistics")
    public ResponseEntity<Void> clearStatistics() {
        statisticsService.clearStatistics();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/recovery/{uploadId}")
    public ResponseEntity<UploadRecovery> initializeRecovery(
            @PathVariable String uploadId,
            @RequestParam("originalFileName") String originalFileName,
            @RequestParam("fileSize") long fileSize,
            @RequestParam("errorType") String errorType,
            @RequestParam("errorMessage") String errorMessage) {

        UploadRecovery recovery = recoveryService.initializeRecovery(
                uploadId, originalFileName, fileSize, errorType, errorMessage);
        return ResponseEntity.ok(recovery);
    }

    @PostMapping("/recovery/{recoveryId}/attempt")
    public ResponseEntity<UploadRecovery> attemptRecovery(
            @PathVariable String recoveryId,
            @RequestParam("strategy") String strategy) {

        recoveryService.attemptRecovery(recoveryId, strategy);
        UploadRecovery recovery = recoveryService.getRecovery(recoveryId);
        return ResponseEntity.ok(recovery);
    }

    @GetMapping("/recovery/{recoveryId}")
    public ResponseEntity<UploadRecovery> getRecovery(@PathVariable String recoveryId) {
        UploadRecovery recovery = recoveryService.getRecovery(recoveryId);
        if (recovery == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recovery);
    }

    @GetMapping("/recoveries")
    public ResponseEntity<List<UploadRecovery>> getRecoveries(
            @RequestParam(value = "status", required = false) UploadRecovery.RecoveryStatus status) {

        if (status != null) {
            return ResponseEntity.ok(recoveryService.getRecoveriesByStatus(status));
        }
        return ResponseEntity.ok(recoveryService.getPendingRecoveries());
    }

    @GetMapping("/recoveries/metrics")
    public ResponseEntity<Map<String, Object>> getRecoveryMetrics() {
        return ResponseEntity.ok(recoveryService.getRecoveryMetrics());
    }

    @DeleteMapping("/recoveries")
    public ResponseEntity<Void> clearRecoveries() {
        recoveryService.clearRecoveries();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/validate")
    public ResponseEntity<FileValidation> validateFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("uploadId") String uploadId) {

        FileValidation validation = validationService.validateFile(uploadId, file);
        return ResponseEntity.ok(validation);
    }

    @GetMapping("/validate/{validationId}")
    public ResponseEntity<FileValidation> getValidation(@PathVariable String validationId) {
        FileValidation validation = validationService.getValidation(validationId);
        if (validation == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(validation);
    }

    @GetMapping("/validations")
    public ResponseEntity<List<FileValidation>> getValidations(
            @RequestParam(value = "status", required = false) ValidationStatus status) {

        if (status != null) {
            return ResponseEntity.ok(validationService.getValidationsByStatus(status));
        }
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping("/validations/metrics")
    public ResponseEntity<Map<String, Object>> getValidationMetrics() {
        return ResponseEntity.ok(validationService.getValidationMetrics());
    }

    @DeleteMapping("/validations")
    public ResponseEntity<Void> clearValidations() {
        validationService.clearValidations();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/optimize")
    public ResponseEntity<UploadOptimization> optimizeUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("uploadId") String uploadId) {

        UploadOptimization optimization = optimizationService.optimizeUpload(uploadId, file);
        return ResponseEntity.ok(optimization);
    }

    @GetMapping("/optimize/{optimizationId}")
    public ResponseEntity<UploadOptimization> getOptimization(@PathVariable String optimizationId) {
        UploadOptimization optimization = optimizationService.getOptimization(optimizationId);
        if (optimization == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(optimization);
    }

    @GetMapping("/optimizations")
    public ResponseEntity<List<UploadOptimization>> getOptimizations(
            @RequestParam(value = "strategy", required = false) String strategy) {

        if (strategy != null) {
            return ResponseEntity.ok(optimizationService.getOptimizationsByStrategy(strategy));
        }
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping("/optimizations/metrics")
    public ResponseEntity<Map<String, Object>> getOptimizationMetrics() {
        return ResponseEntity.ok(optimizationService.getOptimizationMetrics());
    }

    @DeleteMapping("/optimizations")
    public ResponseEntity<Void> clearOptimizations() {
        optimizationService.clearOptimizations();
        return ResponseEntity.ok().build();
    }
}