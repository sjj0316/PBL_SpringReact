package com.example.portal.repository;

import com.example.portal.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // 카테고리별 게시글 목록 조회
    Page<Post> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.deleted = false")
    Page<Post> findAllByDeletedFalse(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.deleted = false AND p.title LIKE %:keyword%")
    Page<Post> findByTitleContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.deleted = false AND p.content LIKE %:keyword%")
    Page<Post> findByContentContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.deleted = false AND p.user.name LIKE %:keyword%")
    Page<Post> findByUserNameContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.deleted = false AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> findByTitleOrContentContaining(@Param("keyword") String keyword, Pageable pageable);

    // 작성자로 검색
    Page<Post> findByUserUsernameContaining(String keyword, Pageable pageable);

    // 제목 또는 내용으로 검색
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
    Page<Post> findByTitleContainingOrContentContaining(@Param("keyword") String keyword, Pageable pageable);

    // 최근 게시물 조회
    Page<Post> findTop10ByOrderByCreatedAtDesc(Pageable pageable);

    // 인기 게시물 조회
    Page<Post> findTop10ByOrderByViewCountDesc(Pageable pageable);

    // 카테고리별 최근 게시물 조회
    Page<Post> findTop5ByCategoryNameOrderByCreatedAtDesc(String categoryName, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
    Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
