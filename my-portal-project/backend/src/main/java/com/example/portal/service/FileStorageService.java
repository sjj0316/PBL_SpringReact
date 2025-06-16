package com.example.portal.service;

import com.example.portal.dto.FileMetadata;
import com.example.portal.dto.FileValidation;
import com.example.portal.dto.UploadOptimization;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileStorageService {
    FileMetadata storeFile(MultipartFile file) throws IOException;

    Resource loadFileAsResource(String fileId) throws IOException;

    void deleteFile(String fileId) throws IOException;

    boolean exists(String fileId);

    long getFileSize(String fileId) throws IOException;

    FileMetadata getFileMetadata(String fileId);

    List<FileMetadata> getAllFiles();

    void restoreFile(String fileId) throws IOException;

    List<FileMetadata> searchFiles(String keyword);

    byte[] getFileContent(String fileId) throws IOException;

    void updateFileMetadata(FileMetadata metadata);

    boolean isFileExists(String fileId);

    FileValidation validateFile(String fileId);

    UploadOptimization optimizeFile(String fileId) throws IOException;
}