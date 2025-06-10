package com.example.portal.controller;

import com.example.portal.dto.LoginRequest;
import com.example.portal.dto.SignupRequest;
import com.example.portal.dto.TokenResponse;
import com.example.portal.entity.User;
import com.example.portal.security.JwtTokenProvider;
import com.example.portal.service.AuthService;
import com.example.portal.service.RefreshTokenService;
import com.example.portal.security.UserPrincipal;
import com.example.portal.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일")
    })
    @PostMapping("/signup")
    public ResponseEntity<TokenResponse> signup(
            @Parameter(description = "회원가입 정보", required = true) @Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Parameter(description = "로그인 정보", required = true) @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 갱신 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰")
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @Parameter(description = "리프레시 토큰", required = true) @RequestHeader("Refresh-Token") String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @Operation(summary = "로그아웃", description = "현재 사용자의 리프레시 토큰을 무효화합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(description = "리프레시 토큰", required = true) @RequestHeader("Refresh-Token") String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        if (jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
