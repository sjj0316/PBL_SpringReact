package com.example.portal.security;

import com.example.portal.entity.User;
import com.example.portal.repository.UserRepository;
import com.example.portal.security.JwtTokenProvider;
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
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = getEmailFromOAuth2User(oAuth2User);
            final String name = StringUtils.hasText(getNameFromOAuth2User(oAuth2User))
                    ? getNameFromOAuth2User(oAuth2User)
                    : email.split("@")[0];
            String provider = getProviderFromOAuth2User(oAuth2User);

            if (!StringUtils.hasText(email)) {
                log.error("OAuth2 로그인 실패: 이메일 정보 없음");
                handleAuthenticationFailure(request, response, "이메일 정보를 찾을 수 없습니다.");
                return;
            }

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
            log.info("OAuth2 로그인 성공: email={}, provider={}", email, provider);

            // JWT 토큰 생성
            String token = jwtTokenProvider.createToken(user.getEmail());

            // 프론트엔드로 리다이렉트 (토큰 포함)
            String redirectUrl = String.format("%s?token=%s", redirectUri, token);
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 오류 발생", e);
            handleAuthenticationFailure(request, response, "인증 처리 중 오류가 발생했습니다.");
        }
    }

    private void handleAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, String message)
            throws IOException {
        String failureUrl = String.format("%s?error=%s", redirectUri, message);
        getRedirectStrategy().sendRedirect(request, response, failureUrl);
    }

    private String getEmailFromOAuth2User(OAuth2User oAuth2User) {
        try {
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
        } catch (Exception e) {
            log.error("OAuth2 사용자 이메일 추출 중 오류", e);
            return null;
        }
    }

    private String getNameFromOAuth2User(OAuth2User oAuth2User) {
        try {
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
        } catch (Exception e) {
            log.error("OAuth2 사용자 이름 추출 중 오류", e);
            return null;
        }
    }

    private String getProviderFromOAuth2User(OAuth2User oAuth2User) {
        try {
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
        } catch (Exception e) {
            log.error("OAuth2 제공자 추출 중 오류", e);
            return "unknown";
        }
    }
}