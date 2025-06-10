package com.example.portal.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import java.util.List;

@Getter
@Schema(description = "게시글 요청")
public class PostRequest {
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
    @Schema(description = "게시글 제목", example = "게시글 제목입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 4000, message = "내용은 4000자를 초과할 수 없습니다.")
    @Schema(description = "게시글 내용", example = "게시글 내용입니다.")
    private String content;

    @Schema(description = "첨부파일 ID 목록")
    private List<Long> fileIds;
}