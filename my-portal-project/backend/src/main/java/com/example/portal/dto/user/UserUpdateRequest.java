package com.example.portal.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "사용자 정보 수정 요청")
public class UserUpdateRequest {

    @NotBlank(message = "현재 비밀번호는 필수 입력값입니다.")
    @Schema(description = "현재 비밀번호", example = "currentPassword123")
    private String currentPassword;

    @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하로 입력해주세요.")
    @Schema(description = "새 비밀번호", example = "newPassword123")
    private String password;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(min = 2, max = 50, message = "닉네임은 2자 이상 50자 이하로 입력해주세요.")
    @Schema(description = "닉네임", example = "홍길동")
    private String nickname;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;
}