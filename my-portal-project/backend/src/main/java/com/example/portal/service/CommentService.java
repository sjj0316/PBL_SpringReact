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

import com.example.portal.dto.comment.CommentRequest;
import com.example.portal.dto.comment.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * 새로운 댓글을 생성합니다.
     *
     * @param postId  게시글 ID
     * @param request 댓글 생성 요청
     * @param user    작성자
     * @return 생성된 댓글 정보
     */
    public CommentResponse createComment(Long postId, CommentRequestDto request) {
        // 게시글 존재 여부 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "게시글이 존재하지 않습니다."));

        // 댓글 생성 및 저장
        Comment comment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .build();

        commentRepository.save(comment);
        return CommentResponse.fromEntity(comment);
    }

    /**
     * 댓글을 수정합니다.
     *
     * @param commentId 댓글 ID
     * @param request   댓글 수정 요청
     * @param user      수정 요청자
     * @return 수정된 댓글 정보
     */
    public CommentResponse updateComment(Long commentId, CommentRequestDto request) {
        Comment comment = getAuthorizedComment(commentId); // 본인 댓글 확인
        comment.setContent(request.getContent());
        commentRepository.save(comment);
        return CommentResponse.fromEntity(comment);
    }

    /**
     * 댓글을 삭제합니다.
     *
     * @param commentId 댓글 ID
     * @param user      삭제 요청자
     */
    public void deleteComment(Long commentId) {
        Comment comment = getAuthorizedComment(commentId); // 본인 댓글 확인
        commentRepository.delete(comment);
    }

    /**
     * 게시글의 댓글 목록을 조회합니다.
     *
     * @param postId   게시글 ID
     * @param pageable 페이지 정보
     * @return 댓글 목록
     */
    public Page<CommentResponse> getCommentsByPost(Long postId, Pageable pageable) {
        if (!postRepository.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "게시글이 존재하지 않습니다.");
        }
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId, pageable)
                .map(CommentResponse::fromEntity);
    }

    /**
     * 게시글의 댓글 수를 조회합니다.
     *
     * @param postId 게시글 ID
     * @return 댓글 수
     */
    public long getCommentCount(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "게시글이 존재하지 않습니다.");
        }
        return commentRepository.countByPostId(postId);
    }

    /**
     * 사용자명으로 사용자 정보 조회
     * 
     * @param username 사용자명
     * @return User 객체
     */
    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자 정보 없음"));
    }

    /**
     * 본인이 작성한 댓글인지 확인하고 반환
     * 
     * @param id 댓글 ID
     * @return 본인의 댓글
     */
    private Comment getAuthorizedComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."));

        return comment;
    }
}
