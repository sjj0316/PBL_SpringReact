package com.example.portal.service.impl;

import com.example.portal.dto.post.PostRequest;
import com.example.portal.dto.post.PostResponse;
import com.example.portal.dto.post.PostFileResponse;
import com.example.portal.entity.Post;
import com.example.portal.entity.User;
import com.example.portal.entity.PostFile;
import com.example.portal.entity.PostLike;
import com.example.portal.entity.Category;
import com.example.portal.repository.PostRepository;
import com.example.portal.repository.UserRepository;
import com.example.portal.repository.PostFileRepository;
import com.example.portal.repository.PostLikeRepository;
import com.example.portal.repository.CategoryRepository;
import com.example.portal.service.PostService;
import com.example.portal.service.FileStorageService;
import com.example.portal.exception.BusinessException;
import com.example.portal.exception.ErrorCode;
import com.example.portal.security.user.UserPrincipal;
import com.example.portal.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostFileRepository postFileRepository;
    private final PostLikeRepository postLikeRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    @Override
    public PostResponse createPost(PostRequest request, List<MultipartFile> files) {
        UserPrincipal currentUser = SecurityUtil.getCurrentUser();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .build();

        Post savedPost = postRepository.save(post);

        // 파일 업로드 처리
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                try {
                    var fileMetadata = fileStorageService.storeFile(file);
                    PostFile postFile = PostFile.builder()
                            .originalName(fileMetadata.getFileName())
                            .storedName(fileMetadata.getFileId())
                            .url(fileMetadata.getUploadPath())
                            .fileType(fileMetadata.getFileType())
                            .fileSize(fileMetadata.getFileSize())
                            .build();
                    postFile.setPost(savedPost);
                    postFileRepository.save(postFile);
                } catch (IOException e) {
                    throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
                }
            }
        }

        return PostResponse.from(savedPost);
    }

    @Override
    public PostResponse updatePost(Long postId, PostRequest request) {
        UserPrincipal currentUser = SecurityUtil.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        post.update(request.getTitle(), request.getContent(), category);
        Post updatedPost = postRepository.save(post);
        return PostResponse.from(updatedPost);
    }

    @Override
    public void deletePost(Long postId) {
        UserPrincipal currentUser = SecurityUtil.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        postRepository.delete(post);
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        return PostResponse.from(post);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(PostResponse::from);
    }

    @Override
    public void addLike(Long postId) {
        UserPrincipal currentUser = SecurityUtil.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (postLikeRepository.existsByPostAndUser(post, user)) {
            throw new BusinessException(ErrorCode.ALREADY_LIKED);
        }

        PostLike postLike = PostLike.builder()
                .post(post)
                .user(user)
                .build();
        postLikeRepository.save(postLike);
    }

    @Override
    public void removeLike(Long postId) {
        UserPrincipal currentUser = SecurityUtil.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        PostLike postLike = postLikeRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new BusinessException(ErrorCode.LIKE_NOT_FOUND));

        postLikeRepository.delete(postLike);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> searchPosts(String keyword, Pageable pageable) {
        return postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable)
                .map(PostResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> searchPostsByCategory(Long categoryId, String keyword, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        return postRepository.findByCategoryAndTitleContainingOrCategoryAndContentContaining(
                category, keyword, category, keyword, pageable)
                .map(PostResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsByUsername(String username, Pageable pageable) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return postRepository.findByUser(user, pageable)
                .map(PostResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getRecentPosts(Pageable pageable) {
        return postRepository.findByOrderByCreatedAtDesc(pageable)
                .map(PostResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getPopularPosts(Pageable pageable) {
        return postRepository.findByOrderByViewCountDesc(pageable)
                .map(PostResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getRecentPostsByCategory(String categoryName, Pageable pageable) {
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        return postRepository.findByCategoryOrderByCreatedAtDesc(category, pageable)
                .map(PostResponse::from);
    }

    @Override
    public PostFileResponse uploadPostFile(MultipartFile file, Long postId) {
        UserPrincipal currentUser = SecurityUtil.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        try {
            var fileMetadata = fileStorageService.storeFile(file);
            PostFile postFile = PostFile.builder()
                    .originalName(fileMetadata.getFileName())
                    .storedName(fileMetadata.getFileId())
                    .url(fileMetadata.getUploadPath())
                    .fileType(fileMetadata.getFileType())
                    .fileSize(fileMetadata.getFileSize())
                    .build();
            postFile.setPost(post);
            PostFile savedPostFile = postFileRepository.save(postFile);
            return PostFileResponse.from(savedPostFile);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }
}