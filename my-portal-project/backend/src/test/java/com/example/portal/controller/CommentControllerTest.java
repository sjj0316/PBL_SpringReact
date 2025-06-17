package com.example.portal.controller;

import com.example.portal.dto.comment.CommentRequestDto;
import com.example.portal.dto.comment.CommentResponse;
import com.example.portal.service.CommentService;
import com.example.portal.service.RefreshTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import com.example.portal.security.JwtTokenProvider;
import com.example.portal.security.JwtAuthenticationFilter;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(value = CommentController.class, excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
})
class CommentControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private CommentService commentService;

        @MockBean
        private JwtTokenProvider jwtTokenProvider;

        @MockBean
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @MockBean
        private RefreshTokenService refreshTokenService;

        @Test
        @DisplayName("댓글 작성 성공")
        void createCommentSuccess() throws Exception {
                // given
                CommentRequestDto request = new CommentRequestDto();
                request.setContent("테스트 댓글입니다.");

                CommentResponse response = CommentResponse.builder()
                                .id(1L)
                                .content("테스트 댓글입니다.")
                                .author("테스트")
                                .postId(1L)
                                .build();

                given(commentService.createComment(any(Long.class), any(CommentRequestDto.class)))
                                .willReturn(response);

                // when & then
                mockMvc.perform(post("/api/posts/1/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.content").value("테스트 댓글입니다."))
                                .andExpect(jsonPath("$.author").value("테스트"))
                                .andExpect(jsonPath("$.postId").value(1));
        }

        @Test
        @DisplayName("댓글 목록 조회 성공")
        void getCommentsSuccess() throws Exception {
                // given
                CommentResponse comment1 = CommentResponse.builder()
                                .id(1L)
                                .content("첫 번째 댓글")
                                .author("사용자1")
                                .postId(1L)
                                .build();

                CommentResponse comment2 = CommentResponse.builder()
                                .id(2L)
                                .content("두 번째 댓글")
                                .author("사용자2")
                                .postId(1L)
                                .build();

                Page<CommentResponse> commentPage = new PageImpl<>(List.of(comment1, comment2),
                                PageRequest.of(0, 10), 2);

                given(commentService.getCommentsByPost(any(Long.class), any(Pageable.class)))
                                .willReturn(commentPage);

                // when & then
                mockMvc.perform(get("/api/posts/1/comments")
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].id").value(1))
                                .andExpect(jsonPath("$.content[0].content").value("첫 번째 댓글"))
                                .andExpect(jsonPath("$.content[0].author").value("사용자1"))
                                .andExpect(jsonPath("$.content[1].id").value(2))
                                .andExpect(jsonPath("$.content[1].content").value("두 번째 댓글"))
                                .andExpect(jsonPath("$.content[1].author").value("사용자2"));
        }
}