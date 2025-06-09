package com.example.portal.security;

import com.example.portal.entity.User;
import com.example.portal.repository.UserRepository;
import com.example.portal.service.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = getEmailFromOAuth2User(oAuth2User);
        String name = getNameFromOAuth2User(oAuth2User);
        String provider = getProviderFromOAuth2User(oAuth2User);

        // 사용자 정보 저장 또는 업데이트
        User user = userRepository.findByEmail(email)
                .map(existingUser -> {
                    existingUser.setName(name);
                    existingUser.setProvider(provider);
                    return existingUser;
                })
                .orElse(User.builder()
                        .email(email)
                        .name(name)
                        .provider(provider)
                        .build());

        userRepository.save(user);

        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(user.getEmail());

        // 프론트엔드로 리다이렉트 (토큰 포함)
        String redirectUrl = "http://localhost:3000/oauth2/redirect?token=" + token;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private String getEmailFromOAuth2User(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        if (attributes.containsKey("email")) {
            return (String) attributes.get("email");
        }

        // Naver의 경우
        if (attributes.containsKey("response")) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("email");
        }

        // Kakao의 경우
        if (attributes.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            return (String) kakaoAccount.get("email");
        }

        return null;
    }

    private String getNameFromOAuth2User(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        if (attributes.containsKey("name")) {
            return (String) attributes.get("name");
        }

        // Naver의 경우
        if (attributes.containsKey("response")) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("name");
        }

        // Kakao의 경우
        if (attributes.containsKey("properties")) {
            Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
            return (String) properties.get("nickname");
        }

        return null;
    }

    private String getProviderFromOAuth2User(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        if (attributes.containsKey("sub")) {
            return "google";
        }

        if (attributes.containsKey("response")) {
            return "naver";
        }

        if (attributes.containsKey("kakao_account")) {
            return "kakao";
        }

        return "unknown";
    }
}