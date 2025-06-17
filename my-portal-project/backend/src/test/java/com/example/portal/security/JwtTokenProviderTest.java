package com.example.portal.security;

import com.example.portal.entity.User;
import com.example.portal.enums.Role;
import com.example.portal.security.user.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtTokenProviderTest {
    private JwtTokenProvider jwtTokenProvider;
    private UserDetailsServiceImpl userDetailsService;
    private User testUser;

    @BeforeEach
    void setUp() {
        userDetailsService = mock(UserDetailsServiceImpl.class);
        jwtTokenProvider = new JwtTokenProvider(userDetailsService);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "testSecretKey123456789012345678901234567890");
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenDurationMs", 3600000L);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenDurationMs", 86400000L);
        jwtTokenProvider.init();

        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    void createAccessToken_ShouldCreateValidToken() {
        String token = jwtTokenProvider.createAccessToken(testUser);
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(testUser.getEmail(), jwtTokenProvider.getEmailFromToken(token));
    }

    @Test
    void createRefreshToken_ShouldCreateValidToken() {
        String token = jwtTokenProvider.createRefreshToken(testUser);
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertTrue(jwtTokenProvider.isRefreshToken(token));
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        assertFalse(jwtTokenProvider.validateToken("invalid.token.here"));
    }

    @Test
    void getAuthentication_ShouldReturnValidAuthentication() {
        String token = jwtTokenProvider.createAccessToken(testUser);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(testUser.getEmail())).thenReturn(userDetails);

        assertNotNull(jwtTokenProvider.getAuthentication(token));
        verify(userDetailsService).loadUserByUsername(testUser.getEmail());
    }

    @Test
    void getExpirationTime_ShouldReturnValidDate() {
        String token = jwtTokenProvider.createAccessToken(testUser);
        Date expirationTime = jwtTokenProvider.getExpirationTime(token);
        assertNotNull(expirationTime);
        assertTrue(expirationTime.after(new Date()));
    }

    @Test
    void getExpirationTime_WithInvalidToken_ShouldThrowException() {
        assertThrows(RuntimeException.class, () -> jwtTokenProvider.getExpirationTime("invalid.token.here"));
    }
}