// CommentService.java
package com.example.portal.service;

import java.util.Optional;
import com.example.portal.dto.CommentRequestDto;
import com.example.portal.entity.Comment;
import com.example.portal.entity.Post;
import com.example.portal.entity.User;
import com.example.portal.repository.CommentRepository;
import com.example.portal.repository.PostRepository;
import com.example.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * 댓글 관련 비즈니스 로직을 담당하는 서비스 클래스
 * - 댓글 생성, 수정, 삭제, 조회 기능 포함
 * - 사용자 및 게시글 유효성 검사 및 권한 확인 수행
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 댓글 작성 처리
     * @param dto 댓글 요청 DTO (내용, 게시글 ID 포함)
     * @param username 현재 로그인한 사용자명
     */
    public void createComment(CommentRequestDto dto, String username) {
        // 사용자 조회
        User user = getUser(username);

        // 게시글 존재 여부 확인
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "게시글이 존재하지 않습니다."));

        // 댓글 생성 및 저장
        Comment comment = Comment.builder()
                .content(dto.getContent())
                .user(user)
                .post(post)
                .build();

        commentRepository.save(comment);
    }

    /**
     * 댓글 수정 처리
     * @param id 댓글 ID
     * @param dto 수정할 댓글 내용
     * @param username 현재 로그인한 사용자명
     */
    public void updateComment(Long id, CommentRequestDto dto, String username) {
        Comment comment = getAuthorizedComment(id, username); // 본인 댓글 확인
        comment.setContent(dto.getContent());
        commentRepository.save(comment);
    }

    /**
     * 댓글 삭제 처리
     * @param id 댓글 ID
     * @param username 현재 로그인한 사용자명
     */
    public void deleteComment(Long id, String username) {
        Comment comment = getAuthorizedComment(id, username); // 본인 댓글 확인
        commentRepository.delete(comment);
    }

    /**
     * 특정 게시글(postId)에 달린 댓글 목록 조회
     * @param postId 게시글 ID
     * @return 댓글 목록 (작성 시간 오름차순)
     */
    public List<Comment> getCommentsByPost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "게시글이 존재하지 않습니다.");
        }
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    /**
     * 사용자명으로 사용자 정보 조회
     * @param username 사용자명
     * @return User 객체
     */
    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자 정보 없음"));
    }

    /**
     * 본인이 작성한 댓글인지 확인하고 반환
     * @param id 댓글 ID
     * @param username 현재 사용자명
     * @return 본인의 댓글
     */
    private Comment getAuthorizedComment(Long id, String username) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."));

        if (!comment.getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "작성자만 수정/삭제할 수 있습니다.");
        }

        return comment;
    }
}
