package com.example.portal.dto.user;

import com.example.portal.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "사용자 상세 정보 응답")
public class UserInfoResponse {

    @Schema(description = "사용자 ID", example = "1")
    private final Long id;

    @Schema(description = "이메일", example = "user@example.com")
    private final String email;

    @Schema(description = "닉네임", example = "홍길동")
    private final String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private final String profileImageUrl;

    @Schema(description = "생성일시")
    private final LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private final LocalDateTime updatedAt;

    public static UserInfoResponse from(User user) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}