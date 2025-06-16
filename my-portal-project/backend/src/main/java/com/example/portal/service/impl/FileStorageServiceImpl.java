package com.example.portal.service.impl;

import com.example.portal.dto.FileMetadata;
import com.example.portal.dto.FileValidation;
import com.example.portal.dto.UploadOptimization;
import com.example.portal.service.FileStorageService;
import com.example.portal.util.ImageCompressor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);
    private final ImageCompressor imageCompressor;
    private final Path fileStorageLocation;

    public FileStorageServiceImpl(
            @Value("${file.upload.dir:uploads}") String uploadDir,
            ImageCompressor imageCompressor) {
        this.imageCompressor = imageCompressor;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
            logger.info("File storage location created at: {}", this.fileStorageLocation);
        } catch (IOException e) {
            logger.error("Failed to create file storage directory", e);
            throw new RuntimeException("Failed to create file storage directory", e);
        }
    }

    @Override
    public FileMetadata storeFile(MultipartFile file) throws IOException {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFileName.contains("..")) {
            throw new IOException("Invalid file path sequence " + originalFileName);
        }

        String fileId = UUID.randomUUID().toString();
        Path targetLocation = this.fileStorageLocation.resolve(fileId);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            logger.info("File stored successfully: {}", fileId);
            return new FileMetadata(fileId, originalFileName);
        } catch (IOException e) {
            logger.error("Failed to store file: {}", originalFileName, e);
            throw new IOException("Failed to store file " + originalFileName, e);
        }
    }

    @Override
    public Resource loadFileAsResource(String fileId) throws IOException {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileId).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                logger.debug("File resource loaded: {}", fileId);
                return resource;
            } else {
                logger.error("File not found: {}", fileId);
                throw new IOException("File not found " + fileId);
            }
        } catch (MalformedURLException e) {
            logger.error("Invalid file path: {}", fileId, e);
            throw new IOException("Invalid file path " + fileId, e);
        }
    }

    @Override
    public void deleteFile(String fileId) throws IOException {
        Path filePath = this.fileStorageLocation.resolve(fileId).normalize();
        try {
            Files.deleteIfExists(filePath);
            logger.info("File deleted: {}", fileId);
        } catch (IOException e) {
            logger.error("Failed to delete file: {}", fileId, e);
            throw new IOException("Failed to delete file " + fileId, e);
        }
    }

    @Override
    public boolean exists(String fileId) {
        Path filePath = this.fileStorageLocation.resolve(fileId).normalize();
        return Files.exists(filePath);
    }

    @Override
    public long getFileSize(String fileId) throws IOException {
        Path filePath = this.fileStorageLocation.resolve(fileId).normalize();
        try {
            return Files.size(filePath);
        } catch (IOException e) {
            logger.error("Failed to get file size: {}", fileId, e);
            throw new IOException("Failed to get file size " + fileId, e);
        }
    }

    @Override
    public FileMetadata getFileMetadata(String fileId) {
        // TODO: 파일 메타데이터 조회 구현
        return null;
    }

    @Override
    public List<FileMetadata> getAllFiles() {
        // TODO: 모든 파일 목록 조회 구현
        return new ArrayList<>();
    }

    @Override
    public void restoreFile(String fileId) throws IOException {
        // TODO: 파일 복원 구현
    }

    @Override
    public List<FileMetadata> searchFiles(String keyword) {
        // TODO: 파일 검색 구현
        return new ArrayList<>();
    }

    @Override
    public byte[] getFileContent(String fileId) throws IOException {
        // TODO: 파일 내용 조회 구현
        return new byte[0];
    }

    @Override
    public void updateFileMetadata(FileMetadata metadata) {
        // TODO: 파일 메타데이터 업데이트 구현
    }

    @Override
    public boolean isFileExists(String fileId) {
        return exists(fileId);
    }

    @Override
    public FileValidation validateFile(String fileId) {
        // TODO: 파일 유효성 검사 구현
        return null;
    }

    @Override
    public UploadOptimization optimizeFile(String fileId) throws IOException {
        // TODO: 파일 최적화 구현
        return null;
    }
}