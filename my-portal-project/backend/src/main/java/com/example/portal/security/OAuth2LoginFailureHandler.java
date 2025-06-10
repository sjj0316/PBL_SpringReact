package com.example.portal.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        log.error("OAuth2 로그인 실패: {}", exception.getMessage());

        String errorMessage = "소셜 로그인에 실패했습니다.";
        if (exception.getMessage().contains("email")) {
            errorMessage = "이메일 정보를 가져올 수 없습니다.";
        } else if (exception.getMessage().contains("access_denied")) {
            errorMessage = "사용자가 로그인을 취소했습니다.";
        }

        String targetUrl = String.format("%s?error=%s", redirectUri, errorMessage);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}