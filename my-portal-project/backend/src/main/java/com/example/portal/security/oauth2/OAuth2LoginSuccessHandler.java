package com.example.portal.security.oauth2;

import com.example.portal.security.JwtTokenProvider;
import com.example.portal.security.UserPrincipal;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String token = tokenProvider.createToken(authentication);

        log.info("OAuth2 로그인 성공: {}", userPrincipal.getEmail());
        log.info("JWT 토큰 발급: {}", token);

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format("{\"token\":\"%s\"}", token));
    }
}