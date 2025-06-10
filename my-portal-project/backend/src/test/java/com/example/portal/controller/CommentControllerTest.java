package com.example.portal.controller;

import com.example.portal.dto.comment.CommentRequestDto;
import com.example.portal.dto.comment.CommentResponse;
import com.example.portal.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @Test
    @DisplayName("댓글 작성 성공")
    void createCommentSuccess() throws Exception {
        // given
        CommentRequestDto request = new CommentRequestDto();
        request.setContent("테스트 댓글입니다.");

        CommentResponse response = CommentResponse.builder()
                .id(1L)
                .content("테스트 댓글입니다.")
                .authorId(1L)
                .authorName("테스트")
                .postId(1L)
                .build();

        given(commentService.createComment(any(Long.class), any(CommentRequestDto.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/comments/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("테스트 댓글입니다."))
                .andExpect(jsonPath("$.authorId").value(1))
                .andExpect(jsonPath("$.authorName").value("테스트"))
                .andExpect(jsonPath("$.postId").value(1));
    }

    @Test
    @DisplayName("댓글 목록 조회 성공")
    void getCommentsSuccess() throws Exception {
        // given
        CommentResponse response = CommentResponse.builder()
                .id(1L)
                .content("테스트 댓글입니다.")
                .authorId(1L)
                .authorName("테스트")
                .postId(1L)
                .build();

        Page<CommentResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);
        given(commentService.getCommentsByPostId(any(Long.class), any(PageRequest.class)))
                .willReturn(page);

        // when & then
        mockMvc.perform(get("/api/comments/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].content").value("테스트 댓글입니다."))
                .andExpect(jsonPath("$.content[0].authorId").value(1))
                .andExpect(jsonPath("$.content[0].authorName").value("테스트"))
                .andExpect(jsonPath("$.content[0].postId").value(1));
    }
}