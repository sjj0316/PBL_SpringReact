package com.example.portal.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "사용자 프로필 수정 요청")
public class UserProfileUpdateRequest {
    @Schema(description = "이름", example = "홍길동")
    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요.")
    private String name;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;
}