package com.example.portal.dto.category;

import com.example.portal.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "카테고리 응답")
public class CategoryResponse {
    @Schema(description = "카테고리 ID", example = "1")
    private Long id;

    @Schema(description = "카테고리 이름", example = "공지사항")
    private String name;

    @Schema(description = "카테고리 설명", example = "공지사항 게시판입니다.")
    private String description;

    @Schema(description = "정렬 순서", example = "1")
    private Integer displayOrder;

    @Schema(description = "활성화 여부", example = "true")
    private Boolean isActive;

    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .displayOrder(category.getDisplayOrder() != null ? category.getDisplayOrder() : 0)
                .isActive(category.isActive())
                .build();
    }
}