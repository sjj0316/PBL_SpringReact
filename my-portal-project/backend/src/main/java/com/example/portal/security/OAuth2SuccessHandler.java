package com.example.portal.security;

import com.example.portal.entity.User;
import com.example.portal.repository.UserRepository;
import com.example.portal.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = userRepository.findByEmail(oAuth2User.getEmail()).orElseThrow();

        String token = jwtTokenProvider.createToken(authentication);

        // 프론트엔드로 JWT 토큰을 쿼리 파라미터로 전달 (예시)
        response.sendRedirect("http://localhost:5173/oauth2/redirect?token=" + token);
    }
}