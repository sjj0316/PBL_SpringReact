package com.example.portal.dto.comment;

import com.example.portal.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "댓글 응답")
public class CommentResponse {

    @Schema(description = "댓글 ID", example = "1")
    private Long id;

    @Schema(description = "댓글 내용", example = "댓글 내용입니다.")
    private String content;

    @Schema(description = "작성자 ID", example = "1")
    private Long authorId;

    @Schema(description = "작성자 이름", example = "홍길동")
    private String authorName;

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorId(comment.getUser().getId())
                .authorName(comment.getUser().getName())
                .postId(comment.getPost().getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}