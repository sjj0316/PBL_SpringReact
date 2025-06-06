package com.example.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 댓글 작성/수정 요청을 처리하는 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    private Long postId;
    private String content;
}
