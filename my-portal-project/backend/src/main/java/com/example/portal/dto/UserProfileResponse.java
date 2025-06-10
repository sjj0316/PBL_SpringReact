package com.example.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 프로필 정보 응답 DTO
 * 클라이언트에 반환되는 사용자 프로필 정보를 담는 객체입니다.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    /**
     * 사용자 이메일
     */
    private String email;

    /**
     * 사용자 이름
     */
    private String name;

    /**
     * 사용자 역할 (예: ROLE_USER, ROLE_ADMIN)
     */
    private String role;
}