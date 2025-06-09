package com.example.portal.controller;

import com.example.portal.dto.MainPageResponseDto;
import com.example.portal.service.MainPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class MainPageController {

    private final MainPageService mainPageService;

    @GetMapping
    public ResponseEntity<MainPageResponseDto> getMainPageData() {
        return ResponseEntity.ok(mainPageService.getMainPageData());
    }
}