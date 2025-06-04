package com.example.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * Spring Security 설정을 개발용으로 완화하여
     * - Swagger UI
     * - API 테스트 (/api/**)
     * 를 인증 없이 허용하도록 구성한 필터 체인입니다.
     *
     * 🚨 이 설정은 **개발 환경에서만** 사용하고,
     *     운영 환경에서는 반드시 인증/인가를 적용해야 합니다.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF(크로스 사이트 요청 위조) 비활성화: 테스트 시 필요
            .csrf(csrf -> csrf.disable())

            // HTTP 요청 인증 정책 설정
            .authorizeHttpRequests(auth -> auth

                // ✅ Swagger 관련 경로는 인증 없이 허용
                .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()

                // ✅ API 호출도 인증 없이 허용 (개발용)
                .requestMatchers("/api/**").permitAll()

                // 🔒 그 외 요청은 인증 필요 (현재는 모두 허용)
                .anyRequest().permitAll()
            )

            // 기본 로그인/로그아웃 페이지 비활성화 및 HTTP Basic 사용
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
