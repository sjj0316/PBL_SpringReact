package com.example.portal.service.impl;

import com.example.portal.entity.Post;
import com.example.portal.entity.PostFile;
import com.example.portal.exception.FileStorageException;
import com.example.portal.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageServiceImpl(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new FileStorageException("파일 저장 디렉토리를 생성할 수 없습니다.", ex);
        }
    }

    @Override
    public PostFile storeFile(MultipartFile file, Post post) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFileName);
        String storedFileName = UUID.randomUUID().toString() + fileExtension;

        try {
            if (file.isEmpty()) {
                throw new FileStorageException("빈 파일은 업로드할 수 없습니다: " + originalFileName);
            }

            if (originalFileName.contains("..")) {
                throw new FileStorageException("파일명에 잘못된 문자가 포함되어 있습니다: " + originalFileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return PostFile.builder()
                    .originalName(originalFileName)
                    .storedName(storedFileName)
                    .url(getFileUrl(storedFileName))
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .post(post)
                    .build();

        } catch (IOException ex) {
            throw new FileStorageException("파일을 저장할 수 없습니다: " + originalFileName, ex);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new FileStorageException("파일을 삭제할 수 없습니다: " + fileName, ex);
        }
    }

    @Override
    public String getFileUrl(String fileName) {
        return "/api/files/" + fileName;
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
    }
}