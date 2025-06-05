package com.example.portal.repository;

import com.example.portal.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// 시간 정렬
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
}

