package com.example.portal.service.impl;

import com.example.portal.dto.comment.CommentRequestDto;
import com.example.portal.dto.comment.CommentResponse;
import com.example.portal.entity.Comment;
import com.example.portal.entity.Post;
import com.example.portal.entity.User;
import com.example.portal.exception.ResourceNotFoundException;
import com.example.portal.exception.UnauthorizedException;
import com.example.portal.repository.CommentRepository;
import com.example.portal.repository.PostRepository;
import com.example.portal.security.SecurityUtil;
import com.example.portal.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public CommentResponse createComment(Long postId, CommentRequestDto request) {
        User currentUser = SecurityUtil.getCurrentUser().getUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .user(currentUser)
                .build();

        if (request.getParentId() != null) {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("부모 댓글을 찾을 수 없습니다."));
            comment.setParent(parent);
        }

        Comment savedComment = commentRepository.save(comment);
        return CommentResponse.from(savedComment);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long commentId, CommentRequestDto request) {
        User currentUser = SecurityUtil.getCurrentUser().getUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("댓글을 수정할 권한이 없습니다.");
        }

        comment.update(request.getContent());
        return CommentResponse.from(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        User currentUser = SecurityUtil.getCurrentUser().getUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    @Override
    public Page<CommentResponse> getCommentsByPost(Long postId, Pageable pageable) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("게시글을 찾을 수 없습니다.");
        }
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable)
                .map(CommentResponse::from);
    }

    @Override
    public long getCommentCount(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("게시글을 찾을 수 없습니다.");
        }

        return commentRepository.countByPostId(postId);
    }
}