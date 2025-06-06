package com.example.portal;

import com.example.portal.dto.CommentRequestDto;
import com.example.portal.dto.LoginRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = PortalApplication.class)
@AutoConfigureMockMvc
public class CommentControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void 댓글작성_성공() throws Exception {
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

                CommentRequestDto comment = new CommentRequestDto();
                comment.setPostId(1L);
                comment.setContent("테스트 댓글");

                mockMvc.perform(post("/api/comments")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(comment)))
                                .andExpect(status().isOk());
        }

        private String extractToken(String json) {
                int start = json.indexOf(":\"") + 2;
                int end = json.indexOf("\"", start);
                return json.substring(start, end);
        }
}
