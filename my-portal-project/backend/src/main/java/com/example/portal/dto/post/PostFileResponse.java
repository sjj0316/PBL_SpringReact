package com.example.portal.dto.post;

import com.example.portal.entity.PostFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "게시글 첨부파일 응답")
public class PostFileResponse {
    @Schema(description = "파일 ID", example = "1")
    private Long id;

    @Schema(description = "원본 파일명", example = "example.jpg")
    private String originalName;

    @Schema(description = "저장된 파일명", example = "uuid-example.jpg")
    private String storedName;

    @Schema(description = "파일 URL", example = "http://example.com/files/uuid-example.jpg")
    private String url;

    @Schema(description = "파일 타입")
    private String fileType;

    @Schema(description = "파일 크기(바이트)", example = "1024")
    private Long fileSize;

    public static PostFileResponse from(com.example.portal.entity.PostFile file) {
        return PostFileResponse.builder()
                .id(file.getId())
                .originalName(file.getOriginalName() != null ? file.getOriginalName() : "")
                .storedName(file.getStoredName() != null ? file.getStoredName() : "")
                .url(file.getUrl() != null ? file.getUrl() : "")
                .fileType(file.getFileType() != null ? file.getFileType() : "")
                .fileSize(file.getFileSize() != null ? file.getFileSize() : 0L)
                .build();
    }
}