package com.example.portal.controller;

import com.example.portal.dto.user.UserResponse;
import com.example.portal.dto.user.UserUpdateRequest;
import com.example.portal.security.SecurityUtil;
import com.example.portal.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo() {
        return ResponseEntity.ok(userService.getUserInfo(SecurityUtil.getCurrentUser().getId()));
    }

    @Operation(summary = "사용자 정보 수정", description = "현재 로그인한 사용자의 정보를 수정합니다.")
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMyInfo(@Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(SecurityUtil.getCurrentUser().getId(), request));
    }

    @Operation(summary = "계정 삭제", description = "현재 로그인한 사용자의 계정을 삭제합니다.")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount() {
        userService.deleteUser(SecurityUtil.getCurrentUser().getId());
        return ResponseEntity.noContent().build();
    }
}