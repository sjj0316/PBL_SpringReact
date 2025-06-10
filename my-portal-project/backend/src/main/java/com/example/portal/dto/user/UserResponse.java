package com.example.portal.dto.user;

import com.example.portal.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "사용자 정보 응답")
public class UserResponse {

    @Schema(description = "사용자 ID", example = "1")
    private final Long id;

    @Schema(description = "이메일", example = "user@example.com")
    private final String email;

    @Schema(description = "닉네임", example = "홍길동")
    private final String nickname;

    @Schema(description = "생성일시")
    private final LocalDateTime createdAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .createdAt(user.getCreatedAt())
                .build();
    }
}