@Test
void 댓글작성_성공() throws Exception {
    // 1. 로그인 → 토큰 추출
    LoginRequestDto login = new LoginRequestDto("user1", "pass123");
    String token = mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(); // JSON 파싱하여 Bearer 토큰 추출 필요

    // 2. 댓글 작성
    CommentRequestDto comment = new CommentRequestDto(1L, "테스트 댓글");
    mockMvc.perform(post("/api/comments")
                    .header("Authorization", "Bearer " + 실제_토큰_값)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(comment)))
            .andExpect(status().isOk());
}
