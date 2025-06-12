package com.example.portal.service;

import com.example.portal.entity.RefreshToken;
import com.example.portal.entity.User;
import com.example.portal.exception.TokenRefreshException;
import com.example.portal.repository.RefreshTokenRepository;
import com.example.portal.repository.UserRepository;
import com.example.portal.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

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

    public void deleteRefreshToken(String email) {
        refreshTokenRepository.deleteByUserEmail(email);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }
}