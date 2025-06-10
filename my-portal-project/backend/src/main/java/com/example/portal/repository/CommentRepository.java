package com.example.portal.repository;

import com.example.portal.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostIdOrderByCreatedAtDesc(Long postId, Pageable pageable);

    void deleteByPostId(Long postId);

    long countByPostId(Long postId);
}
