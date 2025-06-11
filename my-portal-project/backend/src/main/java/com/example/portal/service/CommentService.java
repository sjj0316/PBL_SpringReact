package com.example.portal.service;

import com.example.portal.dto.comment.CommentRequestDto;
import com.example.portal.dto.comment.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    CommentResponse createComment(Long postId, CommentRequestDto request);

    CommentResponse updateComment(Long commentId, CommentRequestDto request);

    void deleteComment(Long commentId);

    Page<CommentResponse> getCommentsByPost(Long postId, Pageable pageable);

    long getCommentCount(Long postId);
}