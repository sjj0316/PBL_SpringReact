package com.example.portal.dto;

import lombok.Getter;

@Getter
public enum FileUploadStatus {
    PENDING("대기 중"),
    UPLOADING("업로드 중"),
    IN_PROGRESS("진행 중"),
    COMPLETED("완료"),
    CANCELLED("취소됨"),
    FAILED("실패");

    private final String description;

    FileUploadStatus(String description) {
        this.description = description;
    }
}