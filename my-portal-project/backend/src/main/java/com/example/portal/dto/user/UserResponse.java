package com.example.portal.dto.user;

import com.example.portal.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 정보 응답")
public class UserResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "사진", example = "https://example.com/photo.jpg")
    private String picture;

    @Schema(description = "소개", example = "안녕하세요, 홍길동입니다.")
    private String bio;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .picture(user.getPicture())
                .bio(user.getBio())
                .createdAt(user.getCreatedAt())
                .build();
    }
}