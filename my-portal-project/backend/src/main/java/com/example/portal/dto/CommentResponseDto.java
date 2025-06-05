package com.example.portal.dto;

import java.time.LocalDateTime;

public class CommentResponseDto {
    private Long id;
    private String content;
    private String author;
    private LocalDateTime createdAt;

    public CommentResponseDto(Long id, String content, String author, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
    }

    // Getter 생략 (Lombok 사용 시 @Getter 추가 가능)
}












