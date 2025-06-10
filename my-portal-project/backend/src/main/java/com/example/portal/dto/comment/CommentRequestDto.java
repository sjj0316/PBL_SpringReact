package com.example.portal.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "댓글 요청")
public class CommentRequestDto {

    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 1000, message = "내용은 1000자를 초과할 수 없습니다.")
    @Schema(description = "댓글 내용", example = "댓글 내용입니다.")
    private String content;

    @Schema(description = "부모 댓글 ID (대댓글인 경우)")
    private Long parentId;
}