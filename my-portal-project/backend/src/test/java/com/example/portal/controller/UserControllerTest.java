package com.example.portal.controller;

import com.example.portal.dto.user.UserResponse;
import com.example.portal.dto.user.UserUpdateRequest;
import com.example.portal.entity.User;
import com.example.portal.security.SecurityUtil;
import com.example.portal.security.user.UserPrincipal;
import com.example.portal.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import com.example.portal.security.JwtTokenProvider;
import com.example.portal.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Import;
import com.example.portal.config.JpaConfig;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(value = UserController.class, excludeAutoConfiguration = { SecurityAutoConfiguration.class })
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private UserService userService;

        @MockBean
        private SecurityUtil securityUtil;

        @MockBean
        private JwtTokenProvider jwtTokenProvider;

        @MockBean
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        private User testUser;
        private UserResponse testUserResponse;

        @BeforeEach
        void setUp() {
                testUser = User.builder()
                                .id(1L)
                                .email("test@example.com")
                                .nickname("테스트")
                                .createdAt(LocalDateTime.now())
                                .build();

                testUserResponse = UserResponse.builder()
                                .id(testUser.getId())
                                .email(testUser.getEmail())
                                .nickname(testUser.getNickname())
                                .createdAt(testUser.getCreatedAt())
                                .build();

                when(securityUtil.getCurrentUser()).thenReturn(UserPrincipal.create(testUser));
        }

        @Test
        @WithMockUser
        void getMyInfo_Success() throws Exception {
                when(userService.getUserInfo(eq(testUser.getId()))).thenReturn(testUserResponse);

                mockMvc.perform(get("/api/users/me"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(testUser.getId()))
                                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                                .andExpect(jsonPath("$.nickname").value(testUser.getNickname()));
        }

        @Test
        @WithMockUser
        void updateMyInfo_Success() throws Exception {
                UserUpdateRequest request = UserUpdateRequest.builder()
                                .nickname("새닉네임")
                                .currentPassword("currentPassword")
                                .password("newPassword")
                                .build();

                when(userService.updateUser(eq(testUser.getId()), any(UserUpdateRequest.class)))
                                .thenReturn(testUserResponse);

                mockMvc.perform(put("/api/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(testUser.getId()))
                                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                                .andExpect(jsonPath("$.nickname").value(testUser.getNickname()));
        }

        @Test
        @WithMockUser
        void deleteMyAccount_Success() throws Exception {
                mockMvc.perform(delete("/api/users/me"))
                                .andExpect(status().isNoContent());
        }
}