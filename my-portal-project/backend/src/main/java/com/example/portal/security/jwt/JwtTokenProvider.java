package com.example.portal.security.jwt;

import com.example.portal.entity.User;
import com.example.portal.security.user.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-validity-in-seconds}")
    private long jwtAccessTokenValidityInSeconds;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long jwtRefreshTokenValidityInSeconds;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private Key key;

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    public void init() {
        try {
            this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            log.info("JWT 키 초기화 성공");
        } catch (Exception e) {
            log.error("JWT 키 초기화 실패", e);
            throw new RuntimeException("JWT 키 초기화 실패", e);
        }
    }

    public String createToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String authorities = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(userPrincipal.getEmail())
                .claim("id", userPrincipal.getId())
                .claim("authorities", authorities)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String createToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String createAccessToken(String email) {
        try {
            Date now = new Date();
            Date validity = new Date(now.getTime() + jwtAccessTokenValidityInSeconds * 1000);

            return Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(now)
                    .setExpiration(validity)
                    .signWith(key)
                    .compact();
        } catch (Exception e) {
            log.error("Access 토큰 생성 실패", e);
            throw new RuntimeException("Access 토큰 생성 실패", e);
        }
    }

    public String createRefreshToken(String email) {
        try {
            Date now = new Date();
            Date validity = new Date(now.getTime() + jwtRefreshTokenValidityInSeconds * 1000);

            return Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(now)
                    .setExpiration(validity)
                    .signWith(key)
                    .compact();
        } catch (Exception e) {
            log.error("Refresh 토큰 생성 실패", e);
            throw new RuntimeException("Refresh 토큰 생성 실패", e);
        }
    }

    public String getEmailFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            log.error("토큰에서 이메일 추출 실패", e);
            throw new RuntimeException("토큰에서 이메일 추출 실패", e);
        }
    }

    public Authentication getAuthentication(String token) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(getEmailFromToken(token));
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        } catch (Exception e) {
            log.error("인증 정보 생성 실패", e);
            throw new RuntimeException("인증 정보 생성 실패", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("유효하지 않은 JWT 토큰", e);
            return false;
        }
    }
}