package com.example.portal.repository;

import com.example.portal.entity.PostFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {
    List<PostFile> findByPostId(Long postId);

    void deleteByPostId(Long postId);
}