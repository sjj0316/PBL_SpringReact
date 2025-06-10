package com.example.portal.config;

import com.example.portal.security.JwtAuthenticationFilter;
import com.example.portal.security.OAuth2LoginSuccessHandler;
import com.example.portal.security.OAuth2LoginFailureHandler;
import com.example.portal.security.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 설정
 * 
 * 주요 기능:
 * - JWT 기반 인증 설정
 * - 엔드포인트 접근 제어
 * - 비밀번호 인코더 설정
 * - 인증 매니저 설정
 * 
 * @author portal-team
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
        private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
        private final CustomOAuth2UserService customOAuth2UserService;

        /**
         * 보안 필터 체인 설정
         * - CSRF 보호 비활성화 (JWT 사용으로 인해)
         * - 엔드포인트별 접근 권한 설정
         * - JWT 필터 등록
         */
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/auth/**", "/oauth2/**").permitAll()
                                                .anyRequest().authenticated())
                                .oauth2Login(oauth2 -> oauth2
                                                .authorizationEndpoint(endpoint -> endpoint
                                                                .baseUri("/oauth2/authorization"))
                                                .redirectionEndpoint(endpoint -> endpoint
                                                                .baseUri("/login/oauth2/code/*"))
                                                .userInfoEndpoint(endpoint -> endpoint
                                                                .userService(customOAuth2UserService))
                                                .successHandler(oAuth2LoginSuccessHandler)
                                                .failureHandler(oAuth2LoginFailureHandler))
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        /**
         * 인증 매니저 빈 등록
         * 사용자 인증을 처리하는 매니저를 생성합니다.
         */
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
                return configuration.getAuthenticationManager();
        }

        /**
         * 비밀번호 인코더 빈 등록
         * BCrypt 알고리즘을 사용하여 비밀번호를 암호화합니다.
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}