package com.example.portal.service;

import com.example.portal.dto.user.UserResponse;
import com.example.portal.dto.user.UserUpdateRequest;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface UserService {

    /**
     * 사용자 정보를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 사용자 정보
     */
    UserResponse getUserInfo(Long userId);

    /**
     * 사용자 정보를 업데이트합니다.
     * 
     * @param userId  사용자 ID
     * @param request 업데이트할 사용자 정보
     * @return 업데이트된 사용자 정보
     */
    UserResponse updateUser(Long userId, UserUpdateRequest request);

    /**
     * 사용자를 삭제합니다.
     * 
     * @param userId 사용자 ID
     */
    void deleteUser(Long userId);
}