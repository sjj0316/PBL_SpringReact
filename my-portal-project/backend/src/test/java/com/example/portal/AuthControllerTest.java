package com.example.portal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.portal.dto.LoginRequestDto;
import com.example.portal.dto.RegisterRequestDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void 회원가입_성공() throws Exception {
		RegisterRequestDto dto = new RegisterRequestDto("user1", "pass123");
		String json = objectMapper.writeValueAsString(dto);

		mockMvc.perform(post("/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isOk());
	}

	@Test
	void 로그인_성공() throws Exception {
		LoginRequestDto dto = new LoginRequestDto("user1", "pass123");
		String json = objectMapper.writeValueAsString(dto);

		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists());
	}
}
