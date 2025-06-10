package com.example.portal.service.impl;

import com.example.portal.dto.post.PostRequest;
import com.example.portal.dto.post.PostResponse;
import com.example.portal.dto.post.PostFileResponse;
import com.example.portal.entity.Post;
import com.example.portal.entity.User;
import com.example.portal.entity.PostFile;
import com.example.portal.entity.PostLike;
import com.example.portal.exception.ResourceNotFoundException;
import com.example.portal.exception.UnauthorizedException;
import com.example.portal.repository.PostFileRepository;
import com.example.portal.repository.PostLikeRepository;
import com.example.portal.repository.PostRepository;
import com.example.portal.service.FileStorageService;
import com.example.portal.service.PostService;
import com.example.portal.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostFileRepository postFileRepository;
    private final PostLikeRepository postLikeRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public PostResponse createPost(PostRequest request, List<MultipartFile> files) {
        User currentUser = SecurityUtil.getCurrentUser().getUser();
        Post post = Post.from(request, currentUser);
        Post savedPost = postRepository.save(post);
        return PostResponse.from(savedPost);
    }

    @Override
    @Transactional
    public PostResponse updatePost(Long postId, PostRequest request) {
        User currentUser = SecurityUtil.getCurrentUser().getUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("게시글을 수정할 권한이 없습니다.");
        }

        post.update(request.getTitle(), request.getContent());
        return PostResponse.from(post);
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        User currentUser = SecurityUtil.getCurrentUser().getUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("게시글을 삭제할 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    @Override
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));
        return PostResponse.from(post);
    }

    @Override
    public Page<PostResponse> getPosts(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(PostResponse::from);
    }

    @Override
    @Transactional
    public void addLike(Long postId) {
        User currentUser = SecurityUtil.getCurrentUser().getUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));
        post.addLike(currentUser);
    }

    @Override
    @Transactional
    public void removeLike(Long postId) {
        User currentUser = SecurityUtil.getCurrentUser().getUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));
        post.removeLike(currentUser);
    }

    @Override
    public Page<PostResponse> searchPosts(String keyword, Pageable pageable) {
        return postRepository.searchByKeyword(keyword, pageable)
                .map(PostResponse::from);
    }

    @Override
    @Transactional
    public PostFileResponse uploadPostFile(MultipartFile file, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));
        PostFile postFile = fileStorageService.storeFile(file, post);
        return PostFileResponse.from(postFile);
    }
}