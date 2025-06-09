package com.example.portal;

import com.example.portal.dto.CommentRequestDto;
import com.example.portal.dto.LoginRequestDto;
import com.example.portal.dto.PostRequestDto;
import com.example.portal.entity.Post;
import com.example.portal.entity.User;
import com.example.portal.repository.PostRepository;
import com.example.portal.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommentControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PostRepository postRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        private Long testPostId;

        @BeforeEach
        void setUp() {
                // 기존 데이터 삭제
                userRepository.deleteAllInBatch();
                postRepository.deleteAllInBatch();

                // 테스트 사용자 생성
                User user = User.builder()
                                .username("user1")
                                .password(passwordEncoder.encode("pass123"))
                                .role("ROLE_USER")
                                .build();
                userRepository.saveAndFlush(user);

                // 테스트 게시물 생성
                Post post = Post.builder()
                                .title("테스트 게시물")
                                .content("테스트 내용")
                                .user(user)
                                .build();
                postRepository.saveAndFlush(post);
                testPostId = post.getId();
        }

        @Test
        void 댓글작성_성공() throws Exception {
                // 로그인
                LoginRequestDto login = new LoginRequestDto();
                login.setUsername("user1");
                login.setPassword("pass123");

                MvcResult result = mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(login)))
                                .andExpect(status().isOk())
                                .andReturn();

                String responseBody = result.getResponse().getContentAsString();
                String token = extractToken(responseBody);

                // 댓글 작성
                CommentRequestDto comment = new CommentRequestDto();
                comment.setPostId(testPostId);
                comment.setContent("테스트 댓글");

                mockMvc.perform(post("/api/comments")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(comment)))
                                .andExpect(status().isOk());
        }

        private String extractToken(String json) {
                try {
                        JsonNode jsonNode = objectMapper.readTree(json);
                        return jsonNode.get("token").asText();
                } catch (Exception e) {
                        throw new RuntimeException("토큰 추출 실패: " + e.getMessage());
                }
        }
}
