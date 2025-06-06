package com.example.portal.controller;

import java.util.Optional;
import com.example.portal.dto.PostRequestDto;
import com.example.portal.dto.PostResponseDto;
import com.example.portal.entity.Post;
import com.example.portal.entity.User;
import com.example.portal.repository.PostRepository;
import com.example.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 사용자명 추출
    private String getUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null || principal.equals("anonymousUser")) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return ((UserDetails) principal).getUsername();
    }

    // 게시글 작성
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostRequestDto dto) {
        String username = getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "사용자 정보 없음"));

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .user(user)
                .build();

        postRepository.save(post);
        return ResponseEntity.status(CREATED).body("게시글이 등록되었습니다.");
    }

    // 게시글 목록 조회
    @GetMapping
    public ResponseEntity<?> getPosts() {
        List<PostResponseDto> posts = postRepository.findAll().stream()
                .map(p -> new PostResponseDto(
                        p.getId(),
                        p.getTitle(),
                        p.getContent(),
                        p.getUser().getUsername(),
                        p.getCreatedAt()
                )).toList();

        return ResponseEntity.ok(posts);
    }

    // 게시글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "게시글이 존재하지 않습니다."));

        PostResponseDto dto = new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUser().getUsername(),
                post.getCreatedAt()
        );

        return ResponseEntity.ok(dto);
    }
}
