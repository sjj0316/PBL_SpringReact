// CommentController.java
package com.example.portal.controller;

import com.example.portal.dto.CommentRequestDto;
import com.example.portal.dto.CommentResponseDto;
import com.example.portal.entity.Comment;
import com.example.portal.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 🔐 현재 인증된 사용자의 username을 반환하는 메서드
    private String getUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null || principal.equals("anonymousUser")) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return ((UserDetails) principal).getUsername();
    }

    // 💬 댓글 등록 API
    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentRequestDto requestDto) {
        String username = getUsername();
        commentService.createComment(requestDto, username);
        return ResponseEntity.ok("댓글이 등록되었습니다.");
    }

    // 📥 댓글 목록 조회 API
    @GetMapping
    public ResponseEntity<?> getComments(@RequestParam Long postId) {
        List<Comment> comments = commentService.getCommentsByPost(postId);

        // Entity → DTO 변환
        List<CommentResponseDto> response = comments.stream()
                .map(c -> new CommentResponseDto(
                        c.getId(),
                        c.getContent(),
                        c.getUser().getUsername(),
                        c.getCreatedAt()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    // ✏ 댓글 수정 API
    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id,
                                           @RequestBody CommentRequestDto requestDto) {
        String username = getUsername();
        commentService.updateComment(id, requestDto, username);
        return ResponseEntity.ok("댓글이 수정되었습니다.");
    }

    // ❌ 댓글 삭제 API
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        String username = getUsername();
        commentService.deleteComment(id, username);
        return ResponseEntity.ok("댓글이 삭제되었습니다.");
    }
}
