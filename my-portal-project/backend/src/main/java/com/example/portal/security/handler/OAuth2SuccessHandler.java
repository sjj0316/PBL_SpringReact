package com.example.portal.security.handler;

import com.example.portal.entity.User;
import com.example.portal.repository.UserRepository;
import com.example.portal.security.JwtTokenProvider;
import com.example.portal.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String provider = oAuth2User.getAttribute("provider");

            User user = userRepository.findByEmail(email)
                    .map(existingUser -> updateUser(existingUser, name, provider))
                    .orElseGet(() -> createUser(email, name, provider));

            String token = jwtTokenProvider.createAccessToken(user.getEmail());
            String redirectUrl = String.format("%s?token=%s", redirectUri, token);
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 오류 발생", e);
            handleAuthenticationFailure(request, response, "인증 처리 중 오류가 발생했습니다.");
        }
    }

    private User createUser(String email, String name, String provider) {
        User user = User.builder()
                .email(email)
                .name(name)
                .provider(provider)
                .build();
        return userRepository.save(user);
    }

    private User updateUser(User user, String name, String provider) {
        user.setName(name);
        user.setProvider(provider);
        return userRepository.save(user);
    }

    private void handleAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, String message)
            throws IOException {
        String failureUrl = String.format("%s?error=%s", redirectUri, message);
        getRedirectStrategy().sendRedirect(request, response, failureUrl);
    }
}