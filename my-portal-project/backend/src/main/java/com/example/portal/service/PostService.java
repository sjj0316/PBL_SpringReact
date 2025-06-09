package com.example.portal.service;

import com.example.portal.dto.PostRequestDto;
import com.example.portal.entity.Post;
import com.example.portal.entity.User;
import com.example.portal.repository.PostRepository;
import com.example.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 게시글 생성
    @Transactional
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    // 게시글 목록 조회
    public Page<Post> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    // 카테고리별 게시글 목록 조회
    public Page<Post> getPostsByCategory(Long categoryId, Pageable pageable) {
        return postRepository.findByCategoryId(categoryId, pageable);
    }

    // 게시글 검색
    public Page<Post> searchPosts(String keyword, String searchType, Pageable pageable) {
        if (searchType == null || searchType.equals("title")) {
            return postRepository.findByTitleContaining(keyword, pageable);
        } else if (searchType.equals("content")) {
            return postRepository.findByContentContaining(keyword, pageable);
        } else if (searchType.equals("author")) {
            return postRepository.findByUserUsernameContaining(keyword, pageable);
        } else {
            return postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
        }
    }

    // 게시글 상세 조회
    public Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
    }

    // 게시글 수정
    @Transactional
    public Post updatePost(Long id, PostRequestDto dto, String username) {
        Post post = getPost(id);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));

        if (!post.getUser().equals(user)) {
            throw new IllegalStateException("게시글을 수정할 권한이 없습니다.");
        }

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        return post;
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long id, String username) {
        Post post = getPost(id);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));

        if (!post.getUser().equals(user)) {
            throw new IllegalStateException("게시글을 삭제할 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    // 조회수 증가
    @Transactional
    public void incrementViewCount(Long id) {
        Post post = getPost(id);
        post.setViewCount(post.getViewCount() + 1);
    }

    // 좋아요/싫어요 토글
    @Transactional
    public void toggleLike(Long id, String username) {
        Post post = getPost(id);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));

        // TODO: 좋아요/싫어요 로직 구현
        // 현재는 단순히 좋아요 수만 증가/감소
        post.setLikeCount(post.getLikeCount() + 1);
    }
}
