package com.example.portal.security;

import com.example.portal.entity.RefreshToken;
import com.example.portal.entity.User;
import com.example.portal.repository.RefreshTokenRepository;
import com.example.portal.service.RefreshTokenService;
import com.example.portal.security.user.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT 토큰 생성 및 검증을 담당하는 클래스
 * 
 * 주요 기능:
 * - JWT 토큰 생성
 * - 토큰 유효성 검증
 * - 토큰에서 사용자 정보 추출
 * 
 * @author portal-team
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token-validity-in-seconds}")
    private long jwtAccessTokenValidityInMilliseconds;

    @Value("${app.jwt.refresh-token-validity-in-seconds}")
    private long jwtRefreshTokenValidityInMilliseconds;

    @Value("${jwt.refresh-expiration}")
    private long refreshValidityInMilliseconds;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpirationInMillis;

    private final RefreshTokenRepository refreshTokenRepository;
    private Key key;

    /**
     * 초기화 메서드
     * 시크릿 키를 기반으로 JWT 서명에 사용할 키를 생성합니다.
     */
    @PostConstruct
    protected void init() {
        try {
            this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            log.info("JWT key initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize JWT key", e);
            throw new RuntimeException("JWT key initialization failed", e);
        }
    }

    /**
     * JWT 토큰 생성
     * 
     * @param email 사용자 이메일
     * @return 생성된 JWT 토큰
     */
    public String createAccessToken(String email) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtAccessTokenValidityInMilliseconds);

            return Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(key)
                    .compact();
        } catch (Exception e) {
            log.error("Failed to create access token for email: {}", email, e);
            throw new RuntimeException("Failed to create access token", e);
        }
    }

    /**
     * JWT 토큰 생성
     * 
     * @param email 사용자 이메일
     * @return 생성된 JWT 토큰
     */
    public String createRefreshToken(String email) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtRefreshTokenValidityInMilliseconds);

            return Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(key)
                    .compact();
        } catch (Exception e) {
            log.error("Failed to create refresh token for email: {}", email, e);
            throw new RuntimeException("Failed to create refresh token", e);
        }
    }

    /**
     * JWT 토큰으로부터 인증 정보 추출
     * 
     * @param token JWT 토큰
     * @return 인증 정보
     */
    public Authentication getAuthentication(String token) {
        try {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(getEmailFromToken(token));
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        } catch (Exception e) {
            log.error("Failed to get authentication from token", e);
            throw new RuntimeException("Failed to get authentication from token", e);
        }
    }

    /**
     * JWT 토큰에서 사용자 이메일 추출
     * 
     * @param token JWT 토큰
     * @return 사용자 이메일
     */
    public String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            log.error("Failed to get email from token", e);
            throw new RuntimeException("Failed to get email from token", e);
        }
    }

    /**
     * JWT 토큰 유효성 검증
     * 
     * @param token JWT 토큰
     * @return 유효성 여부
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

    /**
     * Authentication 객체로부터 JWT 토큰 생성
     */
    public String createToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String authorities = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtAccessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(userPrincipal.getEmail())
                .claim("id", userPrincipal.getId())
                .claim("authorities", authorities)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(User user) {
        String token = Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshValidityInMilliseconds))
                .signWith(key)
                .compact();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.ofInstant(Instant.now().plusMillis(refreshValidityInMilliseconds),
                        ZoneId.systemDefault()))
                .build();

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    public String refreshAccessToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new JwtException("유효하지 않은 리프레시 토큰입니다.");
        }

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new JwtException("리프레시 토큰을 찾을 수 없습니다."));

        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new JwtException("만료된 리프레시 토큰입니다.");
        }

        return createAccessToken(token.getUser().getEmail());
    }

    public long getRefreshTokenExpirationInMillis() {
        return refreshTokenExpirationInMillis;
    }

    /**
     * 토큰에서 만료일 추출
     */
    public Date getExpirationTime(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration();
        } catch (Exception e) {
            log.error("Failed to get expiration from token", e);
            throw new RuntimeException("Failed to get expiration from token", e);
        }
    }

    // 추가: 토큰에서 사용자명 추출, 토큰 유효성 검사 등 필요한 경우 여기에 작성
}
