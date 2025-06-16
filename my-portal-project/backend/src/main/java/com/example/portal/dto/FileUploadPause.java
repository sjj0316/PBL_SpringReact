package com.example.portal.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadPause {
    private String uploadId;
    private boolean paused;
    private LocalDateTime pauseTime;
    private String reason;
    private long uploadedBytes;
    private long totalBytes;

    public static FileUploadPause of(String uploadId) {
        return FileUploadPause.builder()
                .uploadId(uploadId)
                .paused(false)
                .uploadedBytes(0)
                .totalBytes(0)
                .build();
    }

    public void pause(String reason) {
        this.paused = true;
        this.pauseTime = LocalDateTime.now();
        this.reason = reason;
    }

    public void resume() {
        this.paused = false;
        this.pauseTime = null;
        this.reason = null;
    }

    public int getProgressPercentage() {
        if (totalBytes == 0)
            return 0;
        return (int) ((uploadedBytes * 100) / totalBytes);
    }
}