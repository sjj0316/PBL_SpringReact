package com.example.portal.security;

import com.example.portal.entity.User;
import com.example.portal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.portal.security.oauth2.OAuth2LoginSuccessHandler;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class OAuth2LoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OAuth2LoginSuccessHandler successHandler;

    @Autowired
    private OAuth2LoginFailureHandler failureHandler;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void googleLoginSuccess() throws Exception {
        // Given
        OAuth2User oAuth2User = mock(OAuth2User.class);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "test@google.com");
        attributes.put("name", "Test User");
        attributes.put("sub", "123456789");

        when(oAuth2User.getAttributes()).thenReturn(attributes);

        // When & Then
        mockMvc.perform(get("/oauth2/authorization/google"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    void naverLoginSuccess() throws Exception {
        // Given
        OAuth2User oAuth2User = mock(OAuth2User.class);
        Map<String, Object> response = new HashMap<>();
        response.put("email", "test@naver.com");
        response.put("name", "Test User");

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("response", response);

        when(oAuth2User.getAttributes()).thenReturn(attributes);

        // When & Then
        mockMvc.perform(get("/oauth2/authorization/naver"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    void kakaoLoginSuccess() throws Exception {
        // Given
        OAuth2User oAuth2User = mock(OAuth2User.class);
        Map<String, Object> profile = new HashMap<>();
        profile.put("nickname", "Test User");
        profile.put("profile_image_url", "http://example.com/profile.jpg");

        Map<String, Object> kakaoAccount = new HashMap<>();
        kakaoAccount.put("email", "test@kakao.com");
        kakaoAccount.put("profile", profile);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("kakao_account", kakaoAccount);

        when(oAuth2User.getAttributes()).thenReturn(attributes);

        // When & Then
        mockMvc.perform(get("/oauth2/authorization/kakao"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    void loginFailure() throws Exception {
        // When & Then
        mockMvc.perform(get("/login/oauth2/code/google")
                .param("error", "access_denied"))
                .andExpect(status().is3xxRedirection());
    }
}