package com.example.portal.dto.category;

import com.example.portal.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "카테고리 응답 DTO")
public class CategoryResponseDto {

    @Schema(description = "카테고리 ID")
    private Long id;

    @Schema(description = "카테고리 이름", example = "공지사항")
    private String name;

    @Schema(description = "카테고리 설명", example = "공지사항 게시판입니다.")
    private String description;

    @Schema(description = "생성일")
    private LocalDateTime createdAt;

    @Schema(description = "수정일")
    private LocalDateTime updatedAt;

    @Schema(description = "표시 순서")
    private Integer displayOrder;

    @Schema(description = "활성화 여부")
    private boolean isActive;

    public static CategoryResponseDto from(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .displayOrder(category.getDisplayOrder() != null ? category.getDisplayOrder() : 0)
                .isActive(category.isActive())
                .build();
    }
}