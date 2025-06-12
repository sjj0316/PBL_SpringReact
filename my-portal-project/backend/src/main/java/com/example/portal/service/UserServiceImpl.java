package com.example.portal.service;

import com.example.portal.dto.user.UserResponse;
import com.example.portal.dto.user.UserUpdateRequest;
import com.example.portal.entity.User;
import com.example.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    /**
     * 사용자 정보를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 사용자 정보
     */
    public UserResponse getUserInfo(Long userId) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    /**
     * 사용자 정보를 업데이트합니다.
     * 
     * @param userId  사용자 ID
     * @param request 업데이트할 사용자 정보
     * @return 업데이트된 사용자 정보
     */
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    /**
     * 사용자를 삭제합니다.
     * 
     * @param userId 사용자 ID
     */
    public void deleteUser(Long userId) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}