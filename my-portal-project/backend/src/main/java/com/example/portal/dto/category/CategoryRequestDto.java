package com.example.portal.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "카테고리 요청 DTO")
public class CategoryRequestDto {

    @NotBlank(message = "카테고리 이름은 필수 입력값입니다.")
    @Schema(description = "카테고리 이름", example = "공지사항")
    private String name;

    @Schema(description = "카테고리 설명", example = "공지사항 게시판입니다.")
    private String description;

    private Integer displayOrder;
    private boolean isActive;
}