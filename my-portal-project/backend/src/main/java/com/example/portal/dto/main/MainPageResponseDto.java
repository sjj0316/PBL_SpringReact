package com.example.portal.dto.main;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "메인 페이지 응답 DTO")
public class MainPageResponseDto {
    @Schema(description = "최근 게시글 목록")
    private List<RecentPostDto> recentPosts;

    @Schema(description = "인기 게시글 목록")
    private List<PopularPostDto> popularPosts;

    @Schema(description = "카테고리 목록")
    private List<CategoryDto> categories;

    @Getter
    @Setter
    @Schema(description = "최근 게시글 정보")
    public static class RecentPostDto {
        @Schema(description = "게시글 ID")
        private Long id;

        @Schema(description = "제목")
        private String title;

        @Schema(description = "작성자")
        private String author;

        @Schema(description = "작성일")
        private String createdAt;
    }

    @Getter
    @Setter
    @Schema(description = "인기 게시글 정보")
    public static class PopularPostDto {
        @Schema(description = "게시글 ID")
        private Long id;

        @Schema(description = "제목")
        private String title;

        @Schema(description = "조회수")
        private int viewCount;

        @Schema(description = "좋아요 수")
        private int likeCount;
    }

    @Getter
    @Setter
    @Schema(description = "카테고리 정보")
    public static class CategoryDto {
        @Schema(description = "카테고리 ID")
        private Long id;

        @Schema(description = "카테고리명")
        private String name;

        @Schema(description = "게시글 수")
        private int postCount;
    }
}