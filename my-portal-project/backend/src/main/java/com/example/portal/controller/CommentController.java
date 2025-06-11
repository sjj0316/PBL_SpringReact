// CommentController.java
package com.example.portal.controller;

import com.example.portal.dto.comment.CommentRequestDto;
import com.example.portal.dto.comment.CommentResponse;
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

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "댓글", description = "댓글 관련 API")
public class CommentController {
        private final CommentService commentService;

        @GetMapping("/post/{postId}")
        @Operation(summary = "게시글의 댓글 목록 조회", description = "특정 게시글의 모든 댓글을 조회합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공"),
                        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
                        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        })
        public ResponseEntity<Page<CommentResponse>> getCommentsByPost(
                        @Parameter(description = "게시글 ID", required = true) @PathVariable Long postId,
                        @Parameter(description = "페이지네이션 정보") Pageable pageable) {
                return ResponseEntity.ok(commentService.getCommentsByPost(postId, pageable));
        }

        @PostMapping("/post/{postId}")
        @Operation(summary = "댓글 작성", description = "새로운 댓글을 작성합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "댓글 작성 성공"),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청"),
                        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
                        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        })
        public ResponseEntity<CommentResponse> createComment(
                        @Parameter(description = "게시글 ID", required = true) @PathVariable Long postId,
                        @Parameter(description = "댓글 작성 요청", required = true) @RequestBody CommentRequestDto request) {
                return ResponseEntity.ok(commentService.createComment(postId, request));
        }

        @PutMapping("/{id}")
        @Operation(summary = "댓글 수정", description = "기존 댓글을 수정합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청"),
                        @ApiResponse(responseCode = "403", description = "권한 없음"),
                        @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
                        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        })
        public ResponseEntity<CommentResponse> updateComment(
                        @Parameter(description = "댓글 ID", required = true) @PathVariable Long id,
                        @Parameter(description = "댓글 수정 요청", required = true) @RequestBody CommentRequestDto request) {
                return ResponseEntity.ok(commentService.updateComment(id, request));
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "댓글 삭제 성공"),
                        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청"),
                        @ApiResponse(responseCode = "403", description = "권한 없음"),
                        @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
                        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        })
        public ResponseEntity<Void> deleteComment(
                        @Parameter(description = "댓글 ID", required = true) @PathVariable Long id) {
                commentService.deleteComment(id);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "게시글의 댓글 수 조회", description = "특정 게시글의 댓글 수를 조회합니다.")
        @GetMapping("/posts/{postId}/count")
        public ResponseEntity<Long> getCommentCount(@PathVariable Long postId) {
                return ResponseEntity.ok(commentService.getCommentCount(postId));
        }
}
