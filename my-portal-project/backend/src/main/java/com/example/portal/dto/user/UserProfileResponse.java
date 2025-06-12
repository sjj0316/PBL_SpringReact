package com.example.portal.dto.user;

import com.example.portal.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "사용자 프로필 응답")
public class UserProfileResponse {
    @Schema(description = "사용자 ID", example = "1")
    private final Long id;

    @Schema(description = "사용자 이름", example = "홍길동")
    private final String name;

    @Schema(description = "이메일", example = "user@example.com")
    private final String email;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private final String picture;

    @Schema(description = "자기소개", example = "안녕하세요. 홍길동입니다.")
    private final String bio;

    @Schema(description = "가입일시")
    private final String createdAt;

    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .picture(user.getPicture())
                .bio(user.getBio())
                .createdAt(user.getCreatedAt().toString())
                .build();
    }
}