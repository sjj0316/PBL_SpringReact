package com.example.portal.service;

import com.example.portal.entity.RefreshToken;
import java.util.Optional;

public interface RefreshTokenService {
    Optional<RefreshToken> findByToken(String token);

    RefreshToken createRefreshToken(String email);

    RefreshToken rotateRefreshToken(String requestRefreshToken);

    void deleteRefreshToken(String email);

    RefreshToken verifyExpiration(RefreshToken token);

    Object refreshToken();
}