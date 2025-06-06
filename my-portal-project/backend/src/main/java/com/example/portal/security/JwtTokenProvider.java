package com.example.portal.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    // ✅ application.yml에서 설정한 secret 값을 주입받음
    @Value("${jwt.secret}")
    private String secretKey;

    // JWT 유효 시간 (예: 1시간)
    private final long validityInMilliseconds = 3600000;

    /**
     * 인증 객체를 받아 JWT 토큰을 생성
     */
    public String createToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 추가: 토큰에서 사용자명 추출, 토큰 유효성 검사 등 필요한 경우 여기에 작성
}
