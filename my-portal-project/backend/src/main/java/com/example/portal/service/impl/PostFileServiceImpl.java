package com.example.portal.service.impl;

import com.example.portal.entity.Post;
import com.example.portal.entity.PostFile;
import com.example.portal.repository.PostFileRepository;
import com.example.portal.repository.PostRepository;
import com.example.portal.service.FileStorageService;
import com.example.portal.service.PostFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostFileServiceImpl implements PostFileService {

    private final PostFileRepository postFileRepository;
    private final PostRepository postRepository;
    private final FileStorageService fileStorageService;

    @Override
    public PostFile saveFile(MultipartFile file, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return fileStorageService.storeFile(file, post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostFile> getFilesByPostId(Long postId) {
        return postFileRepository.findByPostId(postId);
    }

    @Override
    public void deleteFile(Long fileId) {
        PostFile postFile = postFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        fileStorageService.deleteFile(postFile.getStoredName());
        postFileRepository.delete(postFile);
    }

    @Override
    @Transactional(readOnly = true)
    public PostFile getFileById(Long fileId) {
        return postFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }
}