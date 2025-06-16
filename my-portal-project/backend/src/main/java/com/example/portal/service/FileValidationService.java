package com.example.portal.service;

import com.example.portal.dto.FileValidation;
import com.example.portal.dto.ValidationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileValidationService {
    private final Map<String, FileValidation> validationMap = new ConcurrentHashMap<>();
    private final UploadStatisticsService statisticsService;

    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "xls", "xlsx", "txt");

    public FileValidation validateFile(String uploadId, MultipartFile file) {
        FileValidation validation = FileValidation.of(
                uploadId,
                file.getOriginalFilename(),
                file.getSize(),
                getFileExtension(file.getOriginalFilename()));
        validationMap.put(validation.getValidationId(), validation);

        try {
            performValidation(validation, file);
        } catch (Exception e) {
            validation.failValidation("Validation failed: " + e.getMessage());
            statisticsService.incrementErrorCount(uploadId);
        }

        return validation;
    }

    private void performValidation(FileValidation validation, MultipartFile file) throws IOException {
        validation.startValidation();

        // 파일 크기 검증
        validateFileSize(validation, file.getSize());

        // 파일 확장자 검증
        validateFileExtension(validation, file.getOriginalFilename());

        // 파일 타입 검증
        validateFileType(validation, file);

        // 악성 코드 검사
        validateMalware(validation, file);

        // 파일 무결성 검증
        validateFileIntegrity(validation, file);

        validation.completeValidation();
    }

    private void validateFileSize(FileValidation validation, long size) {
        boolean passed = size <= MAX_FILE_SIZE;
        validation.addValidationResult("SIZE_CHECK", passed,
                passed ? "File size is within limits" : "File size exceeds maximum limit");
    }

    private void validateFileExtension(FileValidation validation, String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        boolean passed = ALLOWED_EXTENSIONS.contains(extension);
        validation.addValidationResult("EXTENSION_CHECK", passed,
                passed ? "File extension is allowed" : "File extension is not allowed");
    }

    private void validateFileType(FileValidation validation, MultipartFile file) throws IOException {
        String mimeType = file.getContentType();
        boolean passed = mimeType != null && isAllowedMimeType(mimeType);
        validation.addValidationResult("MIME_TYPE_CHECK", passed,
                passed ? "File type is allowed" : "File type is not allowed");
    }

    private void validateMalware(FileValidation validation, MultipartFile file) {
        // 실제 구현에서는 바이러스 검사 엔진 연동
        boolean passed = true; // 임시 구현
        validation.addValidationResult("MALWARE_CHECK", passed,
                passed ? "No malware detected" : "Potential malware detected");
    }

    private void validateFileIntegrity(FileValidation validation, MultipartFile file) throws IOException {
        // 실제 구현에서는 파일 해시 검증 등 수행
        boolean passed = true; // 임시 구현
        validation.addValidationResult("INTEGRITY_CHECK", passed,
                passed ? "File integrity verified" : "File integrity check failed");
    }

    private String getFileExtension(String fileName) {
        if (fileName == null)
            return "";
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1) : "";
    }

    private boolean isAllowedMimeType(String mimeType) {
        return mimeType.startsWith("image/") ||
                mimeType.startsWith("application/pdf") ||
                mimeType.startsWith("application/msword") ||
                mimeType.startsWith("application/vnd.openxmlformats-officedocument") ||
                mimeType.equals("text/plain");
    }

    public FileValidation getValidation(String validationId) {
        return validationMap.get(validationId);
    }

    public List<FileValidation> getValidationsByStatus(ValidationStatus status) {
        return validationMap.values().stream()
                .filter(v -> v.getStatus() == status)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getValidationMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        List<FileValidation> allValidations = new ArrayList<>(validationMap.values());

        metrics.put("totalValidations", allValidations.size());

        Map<ValidationStatus, Long> statusCounts = allValidations.stream()
                .collect(Collectors.groupingBy(
                        FileValidation::getStatus,
                        Collectors.counting()));
        metrics.put("statusCounts", statusCounts);

        long safeFiles = allValidations.stream()
                .filter(FileValidation::isSafe)
                .count();
        double safetyRate = allValidations.isEmpty() ? 0.0 : (double) safeFiles / allValidations.size() * 100;
        metrics.put("safetyRate", safetyRate);

        return metrics;
    }

    public void clearValidations() {
        validationMap.clear();
    }

    public boolean isFileSafe(String fileId) {
        FileValidation validation = validateFile(fileId, null);
        return validation.isSafe();
    }

    public List<FileValidation> getFailedValidations() {
        return validationMap.values().stream()
                .filter(v -> !v.isSafe())
                .collect(Collectors.toList());
    }
}