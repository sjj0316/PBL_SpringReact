package com.example.portal.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileMetadata {
    private String fileId;
    private String fileName;
    private String fileType;
    private long fileSize;
    private String uploadPath;
    private String description;
    private String tags;
    private String category;
    private String accessLevel;
    private boolean publicAccess;
    private boolean deleted;
    private String deleteReason;
    private LocalDateTime uploadTime;
    private LocalDateTime lastModifiedTime;
    private int downloadCount;
    private int viewCount;

    public static FileMetadata of(String fileId, String fileName, String fileType, long fileSize, String uploadPath) {
        FileMetadata metadata = new FileMetadata();
        metadata.setFileId(fileId);
        metadata.setFileName(fileName);
        metadata.setFileType(fileType);
        metadata.setFileSize(fileSize);
        metadata.setUploadPath(uploadPath);
        metadata.setUploadTime(LocalDateTime.now());
        metadata.setLastModifiedTime(LocalDateTime.now());
        metadata.setDownloadCount(0);
        metadata.setViewCount(0);
        metadata.setDeleted(false);
        metadata.setPublicAccess(true);
        return metadata;
    }

    public void incrementDownloadCount() {
        this.downloadCount++;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void markAsDeleted(String reason) {
        this.deleted = true;
        this.deleteReason = reason;
        this.lastModifiedTime = LocalDateTime.now();
    }

    public void restore() {
        this.deleted = false;
        this.deleteReason = null;
        this.lastModifiedTime = LocalDateTime.now();
    }
}