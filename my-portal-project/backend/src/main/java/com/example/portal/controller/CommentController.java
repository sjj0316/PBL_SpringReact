// CommentController.java
package com.example.portal.controller;

import com.example.portal.dto.comment.CommentRequestDto;
import com.example.portal.dto.comment.CommentResponse;
import com.example.portal.security.SecurityUtil;
import com.example.portal.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "댓글", description = "댓글 관련 API")
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성", description = "게시글에 새로운 댓글을 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 작성 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @PostMapping("/posts/{postId}")
    public ResponseEntity<CommentResponse> createComment(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long postId,
            @Parameter(description = "댓글 작성 요청", required = true) @RequestBody CommentRequestDto request) {
        return ResponseEntity.ok(commentService.createComment(postId, request));
    }

    @Operation(summary = "댓글 수정", description = "작성한 댓글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @Parameter(description = "댓글 ID", required = true) @PathVariable Long commentId,
            @Parameter(description = "댓글 수정 요청", required = true) @RequestBody CommentRequestDto request) {
        return ResponseEntity.ok(commentService.updateComment(commentId, request));
    }

    @Operation(summary = "댓글 삭제", description = "작성한 댓글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "댓글 ID", required = true) @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글의 댓글 목록 조회", description = "특정 게시글의 댓글 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @GetMapping("/posts/{postId}")
    public ResponseEntity<Page<CommentResponse>> getCommentsByPost(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long postId,
            @Parameter(description = "페이지 정보") Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId, pageable));
    }
}
