package com.example.portal;

import com.example.portal.dto.RegisterRequestDto;
import com.example.portal.entity.User;
import com.example.portal.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.portal.dto.auth.LoginRequest;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		System.out.println("=== 테스트 시작 ===");
		userRepository.deleteAll();

		User testUser = new User();
		testUser.setNickname("testuser");
		testUser.setPassword(passwordEncoder.encode("password123"));
		testUser.setRole(User.Role.ROLE_USER);

		User savedUser = userRepository.save(testUser);
		userRepository.flush(); // DB에 즉시 반영

		System.out.println("저장된 사용자: " + savedUser);
		System.out.println("=== 테스트 설정 완료 ===");
	}

	@Test
	void 로그인_성공() throws Exception {
		LoginRequest loginRequest = new LoginRequest("testuser", "password123");

		mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists());
	}

	@Test
	void 회원가입_테스트() throws Exception {
		RegisterRequestDto registerRequest = new RegisterRequestDto("newuser", "password123");

		mockMvc.perform(post("/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(registerRequest)))
				.andExpect(status().isOk());
	}

	@Test
	void 로그인_테스트() throws Exception {
		LoginRequest loginRequest = new LoginRequest("testuser", "password123");

		mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists());
	}
}
