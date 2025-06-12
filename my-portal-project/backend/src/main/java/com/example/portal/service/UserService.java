package com.example.portal.service;

import com.example.portal.dto.user.UserResponse;
import com.example.portal.dto.user.UserUpdateRequest;
import com.example.portal.entity.User;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface UserService {
    User findByEmail(String email);

    UserResponse getUserInfo(Long userId);

    UserResponse updateUser(Long userId, UserUpdateRequest request);

    void deleteUser(Long userId);
}