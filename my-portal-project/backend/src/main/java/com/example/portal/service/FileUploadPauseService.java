package com.example.portal.service;

import com.example.portal.dto.FileUploadPause;
import com.example.portal.dto.FileUploadProgress;
import com.example.portal.dto.FileUploadStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class FileUploadPauseService {
    private final FileUploadProgressService progressService;
    private final Map<String, FileUploadPause> pauseMap = new ConcurrentHashMap<>();

    public FileUploadPause initializePause(String uploadId, long totalBytes) {
        FileUploadPause pause = FileUploadPause.of(uploadId, 0, totalBytes);
        pauseMap.put(uploadId, pause);
        return pause;
    }

    public FileUploadPause getPauseInfo(String uploadId) {
        return pauseMap.get(uploadId);
    }

    public FileUploadPause pauseUpload(String uploadId, String reason) {
        FileUploadPause pause = pauseMap.get(uploadId);
        if (pause != null) {
            pause = pause.pause(reason);
            pauseMap.put(uploadId, pause);

            // 진행률 업데이트
            progressService.updateProgress(uploadId, FileUploadProgress.builder()
                    .fileName(pause.getUploadId())
                    .status(FileUploadStatus.PENDING)
                    .errorMessage("일시 중지됨: " + reason)
                    .build());
        }
        return pause;
    }

    public FileUploadPause resumeUpload(String uploadId) {
        FileUploadPause pause = pauseMap.get(uploadId);
        if (pause != null) {
            pause = pause.resume();
            pauseMap.put(uploadId, pause);

            // 진행률 업데이트
            progressService.updateProgress(uploadId, FileUploadProgress.builder()
                    .fileName(pause.getUploadId())
                    .status(FileUploadStatus.UPLOADING)
                    .build());
        }
        return pause;
    }

    public void updateUploadProgress(String uploadId, long bytesUploaded) {
        FileUploadPause pause = pauseMap.get(uploadId);
        if (pause != null) {
            pause = pause.updateProgress(bytesUploaded);
            pauseMap.put(uploadId, pause);
        }
    }

    public boolean isPaused(String uploadId) {
        FileUploadPause pause = pauseMap.get(uploadId);
        return pause != null && pause.isPaused();
    }

    public void clearPauseInfo(String uploadId) {
        pauseMap.remove(uploadId);
    }
}