package com.example.portal.dto.post;

import com.example.portal.entity.PostFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "게시글 첨부파일 응답")
public class PostFileResponse {
    @Schema(description = "파일 ID", example = "1")
    private Long id;

    @Schema(description = "원본 파일명", example = "example.jpg")
    private String originalFileName;

    @Schema(description = "저장된 파일명", example = "uuid-example.jpg")
    private String storedFileName;

    @Schema(description = "파일 URL", example = "http://example.com/files/uuid-example.jpg")
    private String fileUrl;

    @Schema(description = "파일 타입")
    private final String fileType;

    @Schema(description = "파일 크기(바이트)", example = "1024")
    private Long fileSize;

    public static PostFileResponse from(PostFile file) {
        return PostFileResponse.builder()
                .id(file.getId())
                .originalFileName(file.getOriginalName())
                .storedFileName(file.getStoredName())
                .fileUrl(file.getFileUrl())
                .fileType(file.getFileType())
                .fileSize(file.getFileSize())
                .build();
    }
}