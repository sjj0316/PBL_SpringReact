package com.example.portal.service.impl;

import com.example.portal.dto.user.UserResponse;
import com.example.portal.dto.user.UserUpdateRequest;
import com.example.portal.entity.User;
import com.example.portal.exception.BusinessException;
import com.example.portal.exception.UnauthorizedException;
import com.example.portal.repository.UserRepository;
import com.example.portal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다."));
        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다."));

        // 비밀번호 변경 시 현재 비밀번호 확인
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new UnauthorizedException("현재 비밀번호가 일치하지 않습니다.");
            }
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // 닉네임 변경
        if (request.getNickname() != null && !request.getNickname().isEmpty()) {
            user.updateNickname(request.getNickname());
        }

        return UserResponse.from(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다."));
        userRepository.delete(user);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다."));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다."));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public User createUser(User user) {
        if (existsByUsername(user.getUsername())) {
            throw new IllegalStateException("이미 존재하는 사용자명입니다.");
        }
        if (existsByEmail(user.getEmail())) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }
        return userRepository.save(user);
    }
}