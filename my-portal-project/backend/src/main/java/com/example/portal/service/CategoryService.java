package com.example.portal.service;

import com.example.portal.dto.CategoryRequestDto;
import com.example.portal.dto.CategoryResponseDto;
import com.example.portal.entity.Category;
import com.example.portal.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 카테고리 생성
    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {
        Category category = Category.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .displayOrder(requestDto.getDisplayOrder())
                .isActive(requestDto.getIsActive())
                .build();

        Category savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }

    // 카테고리 수정
    @Transactional
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto requestDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        category.setName(requestDto.getName());
        category.setDescription(requestDto.getDescription());
        category.setDisplayOrder(requestDto.getDisplayOrder());
        category.setIsActive(requestDto.getIsActive());

        return convertToDto(category);
    }

    // 카테고리 삭제
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
        categoryRepository.delete(category);
    }

    // 카테고리 목록 조회
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 활성화된 카테고리 목록 조회
    public List<CategoryResponseDto> getActiveCategories() {
        return categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 카테고리 상세 조회
    public CategoryResponseDto getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
        return convertToDto(category);
    }

    private CategoryResponseDto convertToDto(Category category) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setDisplayOrder(category.getDisplayOrder());
        dto.setIsActive(category.getIsActive());
        return dto;
    }
}