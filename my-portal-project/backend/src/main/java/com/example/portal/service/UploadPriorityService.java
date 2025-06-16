package com.example.portal.service;

import com.example.portal.dto.UploadPriority;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

@Service
@RequiredArgsConstructor
public class UploadPriorityService {
    private final Map<String, UploadPriority> priorityMap = new ConcurrentHashMap<>();
    private final PriorityBlockingQueue<UploadPriority> priorityQueue = new PriorityBlockingQueue<>(
            100, Comparator.comparingInt(UploadPriority::getPriority).reversed());

    public void setPriority(UploadPriority priority) {
        priorityMap.put(priority.getUploadId(), priority);
        priorityQueue.offer(priority);
    }

    public UploadPriority getPriority(String uploadId) {
        return priorityMap.get(uploadId);
    }

    public void removePriority(String uploadId) {
        priorityMap.remove(uploadId);
        priorityQueue.removeIf(p -> p.getUploadId().equals(uploadId));
    }

    public List<UploadPriority> getPrioritizedUploads() {
        return new ArrayList<>(priorityQueue);
    }

    public UploadPriority calculatePriority(String uploadId, MultipartFile file) {
        int priority = 0;
        UploadPriority.PriorityType type;

        // 파일 크기 기반 우선순위
        long fileSize = file.getSize();
        if (fileSize > 100 * 1024 * 1024) { // 100MB 이상
            priority += 3;
        } else if (fileSize > 10 * 1024 * 1024) { // 10MB 이상
            priority += 2;
        } else if (fileSize > 1 * 1024 * 1024) { // 1MB 이상
            priority += 1;
        }

        // 파일 유형 기반 우선순위
        String contentType = file.getContentType();
        if (contentType != null) {
            if (contentType.startsWith("image/")) {
                priority += 2;
                type = UploadPriority.PriorityType.TYPE_BASED;
            } else if (contentType.startsWith("video/")) {
                priority += 3;
                type = UploadPriority.PriorityType.TYPE_BASED;
            } else if (contentType.startsWith("application/pdf")) {
                priority += 1;
                type = UploadPriority.PriorityType.TYPE_BASED;
            } else {
                type = UploadPriority.PriorityType.SIZE_BASED;
            }
        } else {
            type = UploadPriority.PriorityType.SIZE_BASED;
        }

        return UploadPriority.of(uploadId, priority, type);
    }

    public void updatePriority(String uploadId, int newPriority) {
        UploadPriority existingPriority = priorityMap.get(uploadId);
        if (existingPriority != null) {
            UploadPriority updatedPriority = UploadPriority.of(
                    uploadId,
                    newPriority,
                    existingPriority.getType());
            setPriority(updatedPriority);
        }
    }

    public void clearPriorities() {
        priorityMap.clear();
        priorityQueue.clear();
    }

    public boolean hasHigherPriority(String uploadId1, String uploadId2) {
        UploadPriority priority1 = priorityMap.get(uploadId1);
        UploadPriority priority2 = priorityMap.get(uploadId2);

        if (priority1 == null || priority2 == null) {
            return false;
        }

        return priority1.getPriority() > priority2.getPriority();
    }

    public List<UploadPriority> getPrioritiesByType(UploadPriority.PriorityType type) {
        return priorityMap.values().stream()
                .filter(p -> p.getType() == type)
                .sorted(Comparator.comparingInt(UploadPriority::getPriority).reversed())
                .toList();
    }
}