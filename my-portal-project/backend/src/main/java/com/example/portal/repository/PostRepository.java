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

        @Query("SELECT p FROM Post p WHERE p.isDeleted = false")
        Page<Post> findAllByDeletedFalse(Pageable pageable);

        // 통합 검색 쿼리
        @Query("SELECT p FROM Post p WHERE p.isDeleted = false AND " +
                        "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(p.user.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
        Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

        // 카테고리별 검색
        @Query("SELECT p FROM Post p WHERE p.isDeleted = false AND p.category.id = :categoryId AND " +
                        "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
        Page<Post> searchByCategoryAndKeyword(
                        @Param("categoryId") Long categoryId,
                        @Param("keyword") String keyword,
                        Pageable pageable);

        // 작성자별 검색
        @Query("SELECT p FROM Post p WHERE p.isDeleted = false AND p.user.email = :email")
        Page<Post> findByEmail(@Param("email") String email, Pageable pageable);

        // 최근 게시물 조회
        Page<Post> findTop10ByOrderByCreatedAtDesc(Pageable pageable);

        // 인기 게시물 조회
        Page<Post> findTop10ByOrderByViewCountDesc(Pageable pageable);

        // 카테고리별 최근 게시물 조회
        Page<Post> findTop5ByCategoryNameOrderByCreatedAtDesc(String categoryName, Pageable pageable);
}
