package com.example.portal.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "게시글 응답")
public class PostResponse {

    @Schema(description = "게시글 ID", example = "1")
    private Long id;

    @Schema(description = "게시글 제목", example = "게시글 제목입니다.")
    private String title;

    @Schema(description = "게시글 내용", example = "게시글 내용입니다.")
    private String content;

    @Schema(description = "작성자 ID", example = "1")
    private Long authorId;

    @Schema(description = "작성자 이름", example = "홍길동")
    private String authorName;

    @Schema(description = "좋아요 수", example = "10")
    private int likeCount;

    @Schema(description = "댓글 수", example = "5")
    private int commentCount;

    @Schema(description = "첨부 파일 목록")
    private List<PostFileResponse> files;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    public static PostResponse from(com.example.portal.entity.Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorId(post.getUser().getId())
                .authorName(post.getUser().getName())
                .likeCount(post.getLikes() != null ? post.getLikes().size() : 0)
                .commentCount(post.getComments() != null ? post.getComments().size() : 0)
                .files(post.getFiles() != null ? post.getFiles().stream().map(PostFileResponse::from).toList() : null)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}