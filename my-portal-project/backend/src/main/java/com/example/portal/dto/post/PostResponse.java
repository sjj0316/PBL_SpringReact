package com.example.portal.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 응답 DTO")
public class PostResponse {

    @Schema(description = "게시글 ID")
    private Long id;

    @Schema(description = "게시글 제목")
    private String title;

    @Schema(description = "게시글 내용")
    private String content;

    @Schema(description = "작성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;

    @Schema(description = "작성자")
    private String author;

    @Schema(description = "조회수")
    private int viewCount;

    @Schema(description = "좋아요 수")
    private int likeCount;

    @Schema(description = "카테고리")
    private String category;

    @Schema(description = "첨부파일 목록")
    private List<String> fileUrls;

    @Schema(description = "댓글 수")
    private int commentCount;

    public static PostResponse from(com.example.portal.entity.Post post) {
        List<String> urls = new ArrayList<>();
        if (post.getFiles() != null) {
            for (com.example.portal.entity.PostFile file : post.getFiles()) {
                if (file.getUrl() != null) {
                    urls.add(file.getUrl());
                }
            }
        }

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getUser() != null ? post.getUser().getName() : "")
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikes() != null ? post.getLikes().size() : 0)
                .category(post.getCategory() != null ? post.getCategory().getName() : "")
                .fileUrls(urls)
                .commentCount(post.getComments() != null ? post.getComments().size() : 0)
                .build();
    }
}