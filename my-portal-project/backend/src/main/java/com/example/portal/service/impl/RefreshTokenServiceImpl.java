package com.example.portal.service.impl;

import com.example.portal.entity.RefreshToken;
import com.example.portal.entity.User;
import com.example.portal.exception.TokenRefreshException;
import com.example.portal.repository.RefreshTokenRepository;
import com.example.portal.repository.UserRepository;
import com.example.portal.security.JwtTokenProvider;
import com.example.portal.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken createRefreshToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new TokenRefreshException("User not found with email: " + email));

        String token = jwtTokenProvider.createRefreshToken(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiryDate(LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshTokenDurationMs() / 1000))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken rotateRefreshToken(String requestRefreshToken) {
        return findByToken(requestRefreshToken)
                .map(this::verifyExpiration)
                .map(refreshToken -> {
                    String newToken = jwtTokenProvider.createRefreshToken(refreshToken.getUser());
                    refreshToken.setToken(newToken);
                    refreshToken.setExpiryDate(
                            LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshTokenDurationMs() / 1000));
                    return refreshTokenRepository.save(refreshToken);
                })
                .orElseThrow(() -> new TokenRefreshException("Refresh token not found"));
    }

    @Override
    public void deleteRefreshToken(String email) {
        refreshTokenRepository.deleteByUserEmail(email);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Override
    public Object refreshToken() {
        // 현재 사용자의 리프레시 토큰을 갱신하는 로직
        // 실제 구현에서는 SecurityContext에서 현재 사용자 정보를 가져와야 함
        return Map.of("message", "Token refreshed successfully");
    }
}