package com.example.portal.service;

import com.example.portal.entity.RefreshToken;
import com.example.portal.entity.User;
import com.example.portal.exception.TokenException;
import com.example.portal.repository.RefreshTokenRepository;
import com.example.portal.repository.UserRepository;
import com.example.portal.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // 기존 토큰 삭제
        deleteByUserId(user.getId());

        // 새 토큰 생성
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.ofInstant(
                        java.time.Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenExpirationInMillis()),
                        ZoneId.systemDefault()))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken rotateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Token reuse detected: {}", token);
                    throw new TokenException.TokenReuseException();
                });

        // 토큰이 만료되었는지 확인
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            log.warn("Expired token used: {}", token);
            throw new TokenException.TokenExpiredException();
        }

        // 기존 토큰 삭제
        refreshTokenRepository.delete(refreshToken);

        // 새 토큰 생성
        return createRefreshToken(refreshToken.getUser());
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    @Transactional(readOnly = true)
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Token not found: {}", token);
                    throw new TokenException.TokenNotFoundException();
                });
    }

    // 토큰 재사용 감지를 위한 로깅
    private void logTokenReuse(String token, String userId) {
        log.error("Token reuse detected - Token: {}, UserId: {}", token, userId);
        // TODO: 필요한 경우 알림 발송 또는 추가 조치
    }

    @Transactional
    public void saveRefreshToken(String email, String token) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new TokenException("사용자를 찾을 수 없습니다."));

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiryDate(LocalDateTime.ofInstant(
                        java.time.Instant.now().plusMillis(1000 * 60 * 60 * 24 * 7),
                        ZoneId.systemDefault())) // 7일
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void validateRefreshToken(String email, String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenException("유효하지 않은 리프레시 토큰입니다."));

        if (!refreshToken.getUser().getEmail().equals(email)) {
            throw new TokenException("토큰이 해당 사용자의 것이 아닙니다.");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new TokenException("리프레시 토큰이 만료되었습니다.");
        }
    }

    @Transactional
    public void deleteRefreshToken(String email) {
        refreshTokenRepository.deleteByUserEmail(email);
    }
}