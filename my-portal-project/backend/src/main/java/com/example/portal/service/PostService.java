package com.example.portal.service;

import com.example.portal.dto.post.PostRequest;
import com.example.portal.dto.post.PostResponse;
import com.example.portal.dto.post.PostFileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    PostResponse createPost(PostRequest request, List<MultipartFile> files);

    PostResponse updatePost(Long postId, PostRequest request);

    void deletePost(Long postId);

    PostResponse getPost(Long postId);

    Page<PostResponse> getPosts(Pageable pageable);

    void addLike(Long postId);

    void removeLike(Long postId);

    Page<PostResponse> searchPosts(String keyword, Pageable pageable);

    PostFileResponse uploadPostFile(MultipartFile file, Long postId);
}
