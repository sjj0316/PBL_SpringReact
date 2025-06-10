package com.example.portal.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 사용자 프로필 수정 요청 DTO
 * 클라이언트로부터 받는 프로필 수정 요청 데이터를 담는 객체입니다.
 */
@Getter
@Setter
public class UserProfileUpdateRequest {
    /**
     * 변경할 사용자 이름
     * null이면 변경하지 않음
     */
    private String name;

    /**
     * 현재 비밀번호
     * 비밀번호 변경 시 필수
     */
    private String currentPassword;

    /**
     * 새로운 비밀번호
     * null이면 변경하지 않음
     */
    private String newPassword;
}