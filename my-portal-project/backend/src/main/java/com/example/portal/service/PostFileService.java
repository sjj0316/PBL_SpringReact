package com.example.portal.service;

import com.example.portal.entity.PostFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostFileService {
    PostFile saveFile(MultipartFile file, Long postId);

    List<PostFile> getFilesByPostId(Long postId);

    void deleteFile(Long fileId);

    PostFile getFileById(Long fileId);
}