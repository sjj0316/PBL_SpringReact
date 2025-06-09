package com.example.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainPageResponseDto {
    private List<PostSummaryDto> recentPosts;
    private List<PostSummaryDto> popularPosts;
    private List<PostSummaryDto> notices;
    private List<CategoryDto> categories;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PostSummaryDto {
        private Long id;
        private String title;
        private String author;
        private Integer viewCount;
        private Integer likeCount;
        private LocalDateTime createdAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryDto {
        private Long id;
        private String name;
        private String description;
    }
}