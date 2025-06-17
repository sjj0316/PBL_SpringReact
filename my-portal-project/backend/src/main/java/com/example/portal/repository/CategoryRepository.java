package com.example.portal.repository;

import com.example.portal.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByIsActiveTrueOrderByDisplayOrderAsc();

    Optional<Category> findByName(String name);
}