package com.example.portal.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "카테고리 요청")
public class CategoryRequest {
    @NotBlank(message = "카테고리 이름은 필수 입력값입니다.")
    @Size(min = 2, max = 50, message = "카테고리 이름은 2자 이상 50자 이하로 입력해주세요.")
    @Schema(description = "카테고리 이름", example = "공지사항")
    private String name;

    @Size(max = 200, message = "카테고리 설명은 200자 이하로 입력해주세요.")
    @Schema(description = "카테고리 설명", example = "공지사항 게시판입니다.")
    private String description;

    @Schema(description = "정렬 순서", example = "1")
    private Integer order;
}