package com.example.portal.service;

import com.example.portal.dto.MainPageResponseDto;
import com.example.portal.entity.Category;
import com.example.portal.entity.Post;
import com.example.portal.repository.CategoryRepository;
import com.example.portal.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainPageService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    public MainPageResponseDto getMainPageData() {
        MainPageResponseDto response = new MainPageResponseDto();

        // 최근 게시물
        Pageable recentPageable = PageRequest.of(0, 10);
        List<Post> recentPosts = postRepository.findTop10ByOrderByCreatedAtDesc(recentPageable).getContent();
        response.setRecentPosts(convertToPostSummaryDto(recentPosts));

        // 인기 게시물
        Pageable popularPageable = PageRequest.of(0, 10);
        List<Post> popularPosts = postRepository.findTop10ByOrderByViewCountDesc(popularPageable).getContent();
        response.setPopularPosts(convertToPostSummaryDto(popularPosts));

        // 공지사항
        Pageable noticePageable = PageRequest.of(0, 5);
        List<Post> notices = postRepository.findTop5ByCategoryNameOrderByCreatedAtDesc("공지사항", noticePageable)
                .getContent();
        response.setNotices(convertToPostSummaryDto(notices));

        // 카테고리 목록
        List<Category> categories = categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
        response.setCategories(convertToCategoryDto(categories));

        return response;
    }

    private List<MainPageResponseDto.PostSummaryDto> convertToPostSummaryDto(List<Post> posts) {
        return posts.stream()
                .map(post -> MainPageResponseDto.PostSummaryDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .author(post.getUser().getUsername())
                        .viewCount(post.getViewCount())
                        .likeCount(post.getLikeCount())
                        .createdAt(post.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    private List<MainPageResponseDto.CategoryDto> convertToCategoryDto(List<Category> categories) {
        return categories.stream()
                .map(category -> MainPageResponseDto.CategoryDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .description(category.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}