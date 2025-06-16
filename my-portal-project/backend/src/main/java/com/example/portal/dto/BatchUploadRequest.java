package com.example.portal.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BatchUploadRequest {
    private Long postId;
    private List<String> fileNames;
    private int maxConcurrentUploads;
    private boolean compressImages;
    private String uploadStrategy; // SEQUENTIAL, PARALLEL, PRIORITY

    public enum UploadStrategy {
        SEQUENTIAL, // 순차적 업로드
        PARALLEL, // 병렬 업로드
        PRIORITY // 우선순위 기반 업로드
    }
}