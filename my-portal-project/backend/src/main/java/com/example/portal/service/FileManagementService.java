package com.example.portal.service;

import com.example.portal.dto.FileMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class FileManagementService {
    private static final Logger logger = LoggerFactory.getLogger(FileManagementService.class);
    private final Map<String, FileMetadata> fileMetadataMap = new ConcurrentHashMap<>();
    private final FileStorageService fileStorageService;
    private final Set<String> allowedFileTypes;
    private final long maxFileSize;

    public FileManagementService(
            FileStorageService fileStorageService,
            @Value("${file.allowed-types:*}") String allowedTypes,
            @Value("${file.max-size:104857600}") long maxFileSize) {
        this.fileStorageService = fileStorageService;
        this.allowedFileTypes = new HashSet<>(Arrays.asList(allowedTypes.split(",")));
        this.maxFileSize = maxFileSize;
    }

    public FileMetadata saveFile(MultipartFile file, String uploadPath) throws IOException {
        validateFile(file);

        String fileName = sanitizeFileName(file.getOriginalFilename());
        String fileType = file.getContentType();
        long fileSize = file.getSize();

        String fileId = fileStorageService.storeFile(file).getFileId();
        FileMetadata metadata = FileMetadata.of(fileId, fileName, fileType, fileSize, uploadPath);

        fileMetadataMap.put(fileId, metadata);
        logger.info("File saved successfully: {}", fileId);
        return metadata;
    }

    private void validateFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        if (file.getSize() > maxFileSize) {
            throw new IOException("File size exceeds maximum limit");
        }

        String contentType = file.getContentType();
        if (!allowedFileTypes.contains("*") && !allowedFileTypes.contains(contentType)) {
            throw new IOException("File type not allowed");
        }
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "unnamed_file";
        }
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    public Resource getFileAsResource(String fileId) throws IOException {
        FileMetadata metadata = fileMetadataMap.get(fileId);
        if (metadata == null) {
            throw new IOException("File metadata not found: " + fileId);
        }
        return fileStorageService.loadFileAsResource(fileId);
    }

    public FileMetadata getFileMetadata(String fileId) {
        FileMetadata metadata = fileMetadataMap.get(fileId);
        if (metadata != null) {
            metadata.incrementViewCount();
            logger.debug("File metadata retrieved: {}", fileId);
        }
        return metadata;
    }

    public List<FileMetadata> listFiles(String category, String accessLevel, boolean includeDeleted) {
        return fileMetadataMap.values().stream()
                .filter(metadata -> (category == null || category.equals(metadata.getCategory())))
                .filter(metadata -> (accessLevel == null || accessLevel.equals(metadata.getAccessLevel())))
                .filter(metadata -> includeDeleted || !metadata.isDeleted())
                .collect(Collectors.toList());
    }

    public void deleteFile(String fileId, String reason) throws IOException {
        FileMetadata metadata = fileMetadataMap.get(fileId);
        if (metadata != null) {
            try {
                fileStorageService.deleteFile(fileId);
                metadata.markAsDeleted(reason);
                logger.info("File deleted: {}", fileId);
            } catch (IOException e) {
                logger.error("Failed to delete file: {}", fileId, e);
                throw new IOException("Failed to delete file: " + fileId, e);
            }
        }
    }

    public void restoreFile(String fileId) {
        FileMetadata metadata = fileMetadataMap.get(fileId);
        if (metadata != null) {
            metadata.restore();
            logger.info("File restored: {}", fileId);
        }
    }

    public void updateMetadata(String fileId, FileMetadata newMetadata) {
        FileMetadata existingMetadata = fileMetadataMap.get(fileId);
        if (existingMetadata != null) {
            existingMetadata.setDescription(newMetadata.getDescription());
            existingMetadata.setTags(newMetadata.getTags());
            existingMetadata.setCategory(newMetadata.getCategory());
            existingMetadata.setAccessLevel(newMetadata.getAccessLevel());
            existingMetadata.setPublicAccess(newMetadata.isPublicAccess());
            existingMetadata.setLastModifiedTime(LocalDateTime.now());
            logger.info("File metadata updated: {}", fileId);
        }
    }

    public List<FileMetadata> searchFiles(String query) {
        return fileMetadataMap.values().stream()
                .filter(metadata -> !metadata.isDeleted())
                .filter(metadata -> {
                    String fileName = metadata.getFileName() != null ? metadata.getFileName().toLowerCase() : "";
                    String description = metadata.getDescription() != null ? metadata.getDescription().toLowerCase()
                            : "";
                    String tags = metadata.getTags() != null ? metadata.getTags().toLowerCase() : "";
                    String queryLower = query.toLowerCase();

                    return fileName.contains(queryLower) ||
                            description.contains(queryLower) ||
                            tags.contains(queryLower);
                })
                .collect(Collectors.toList());
    }

    public void incrementDownloadCount(String fileId) {
        FileMetadata metadata = fileMetadataMap.get(fileId);
        if (metadata != null) {
            metadata.incrementDownloadCount();
            logger.debug("Download count incremented for file: {}", fileId);
        }
    }

    public void incrementViewCount(String fileId) {
        FileMetadata metadata = fileMetadataMap.get(fileId);
        if (metadata != null) {
            metadata.incrementViewCount();
            logger.debug("View count incremented for file: {}", fileId);
        }
    }

    public Map<String, Object> getFileStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalFiles", fileMetadataMap.size());
        stats.put("totalSize", fileMetadataMap.values().stream()
                .mapToLong(FileMetadata::getFileSize)
                .sum());
        stats.put("deletedFiles", fileMetadataMap.values().stream()
                .filter(FileMetadata::isDeleted)
                .count());
        stats.put("publicFiles", fileMetadataMap.values().stream()
                .filter(FileMetadata::isPublicAccess)
                .count());
        logger.debug("File statistics retrieved");
        return stats;
    }

    public Path getFilePath(String fileId) throws IOException {
        FileMetadata metadata = fileMetadataMap.get(fileId);
        if (metadata == null) {
            throw new IOException("File metadata not found: " + fileId);
        }
        return fileStorageService.getFilePath(fileId);
    }
}