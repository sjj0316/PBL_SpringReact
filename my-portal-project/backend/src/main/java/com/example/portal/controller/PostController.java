package com.example.portal.controller;

import com.example.portal.dto.PostRequestDto;
import com.example.portal.dto.PostResponseDto;
import com.example.portal.entity.Post;
import com.example.portal.entity.User;
import com.example.portal.service.PostService;
import com.example.portal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserService userService;

    // 게시글 작성
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(
            @RequestBody PostRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .user(user)
                .build();
        Post savedPost = postService.createPost(post);
        return ResponseEntity.ok(convertToDto(savedPost));
    }

    // 게시글 목록 조회 (페이지네이션)
    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> getPosts(Pageable pageable) {
        Page<Post> posts = postService.getPosts(pageable);
        Page<PostResponseDto> postDtos = posts.map(this::convertToDto);
        return ResponseEntity.ok(postDtos);
    }

    // 카테고리별 게시글 목록 조회
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<PostResponseDto>> getPostsByCategory(
            @PathVariable Long categoryId,
            Pageable pageable) {
        Page<Post> posts = postService.getPostsByCategory(categoryId, pageable);
        Page<PostResponseDto> postDtos = posts.map(this::convertToDto);
        return ResponseEntity.ok(postDtos);
    }

    // 게시글 검색
    @GetMapping("/search")
    public ResponseEntity<Page<PostResponseDto>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(required = false) String searchType,
            Pageable pageable) {
        Page<Post> posts = postService.searchPosts(keyword, searchType, pageable);
        Page<PostResponseDto> postDtos = posts.map(this::convertToDto);
        return ResponseEntity.ok(postDtos);
    }

    // 게시글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable Long id) {
        Post post = postService.getPost(id);
        return ResponseEntity.ok(convertToDto(post));
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable Long id,
            @RequestBody PostRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Post post = postService.updatePost(id, dto, userDetails.getUsername());
        return ResponseEntity.ok(convertToDto(post));
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // 조회수 증가
    @PostMapping("/{id}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long id) {
        postService.incrementViewCount(id);
        return ResponseEntity.ok().build();
    }

    // 좋아요/싫어요
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        postService.toggleLike(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    private PostResponseDto convertToDto(Post post) {
        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getUser().getUsername())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
