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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;
    private final long maxFileSize;
    private final List<String> allowedFileTypes;

    public FileStorageServiceImpl(
            @Value("${file.upload-dir}") String uploadDir,
            @Value("${file.max-size:10485760}") long maxFileSize,
            @Value("${file.allowed-types:image/jpeg,image/png,image/gif,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document}") String allowedTypes) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.maxFileSize = maxFileSize;
        this.allowedFileTypes = Arrays.asList(allowedTypes.split(","));
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

            if (file.getSize() > maxFileSize) {
                throw new FileStorageException("파일 크기가 제한을 초과했습니다: " + originalFileName);
            }

            if (!allowedFileTypes.contains(file.getContentType())) {
                throw new FileStorageException("허용되지 않는 파일 형식입니다: " + originalFileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            PostFile postFile = new PostFile();
            postFile.setOriginalName(originalFileName);
            postFile.setStoredName(storedFileName);
            postFile.setUrl(getFileUrl(storedFileName));
            postFile.setFileType(file.getContentType());
            postFile.setFileSize(file.getSize());
            postFile.setPost(post);

            return postFile;

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
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}