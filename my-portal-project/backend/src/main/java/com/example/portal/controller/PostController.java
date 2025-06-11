package com.example.portal.controller;

import com.example.portal.dto.post.PostRequest;
import com.example.portal.dto.post.PostResponse;
import com.example.portal.dto.post.PostFileResponse;
import com.example.portal.entity.User;
import com.example.portal.security.UserPrincipal;
import com.example.portal.security.SecurityUtil;
import com.example.portal.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.util.List;

@Tag(name = "게시글", description = "게시글 관련 API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "게시글 작성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    public ResponseEntity<PostResponse> createPost(
            @Parameter(description = "게시글 작성 정보", required = true) @Valid @RequestPart PostRequest request,
            @Parameter(description = "첨부파일 목록") @RequestPart(required = false) List<MultipartFile> files) {
        return ResponseEntity.ok(postService.createPost(request, files));
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시글 조회", description = "특정 게시글의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    public ResponseEntity<PostResponse> getPost(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    @GetMapping
    @Operation(summary = "게시글 목록 조회", description = "게시글 목록을 페이지네이션하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공")
    })
    public ResponseEntity<Page<PostResponse>> getPosts(
            @Parameter(description = "페이지네이션 정보") Pageable pageable) {
        return ResponseEntity.ok(postService.getPosts(pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    public ResponseEntity<PostResponse> updatePost(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long id,
            @Parameter(description = "게시글 수정 정보", required = true) @Valid @RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.updatePost(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "게시글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "게시글 좋아요", description = "게시글에 좋아요를 추가합니다.")
    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> addLike(@PathVariable Long postId) {
        postService.addLike(postId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "게시글 좋아요 취소", description = "게시글의 좋아요를 취소합니다.")
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> removeLike(@PathVariable Long postId) {
        postService.removeLike(postId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "게시글 검색", description = "키워드로 게시글을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<Page<PostResponse>> searchPosts(
            @RequestParam String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(postService.searchPosts(keyword, pageable));
    }

    @Operation(summary = "게시글 첨부파일 업로드", description = "게시글에 첨부할 파일을 업로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업로드 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청")
    })
    @PostMapping("/upload")
    public ResponseEntity<PostFileResponse> uploadFile(
            @Parameter(description = "인증된 사용자 정보", hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "업로드할 파일", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "게시글 ID", required = true) @RequestParam("postId") Long postId) {
        return ResponseEntity.ok(postService.uploadPostFile(file, postId));
    }
}
