package com.example.portal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadPriority {
    private String uploadId;
    private int priority;
    private PriorityType type;
    private String reason;
    private long timestamp;

    public enum PriorityType {
        USER_DEFINED, // 사용자 정의 우선순위
        SIZE_BASED, // 파일 크기 기반 우선순위
        TYPE_BASED, // 파일 유형 기반 우선순위
        SYSTEM_AUTO // 시스템 자동 우선순위
    }

    public static UploadPriority of(String uploadId, int priority, PriorityType type) {
        UploadPriority uploadPriority = new UploadPriority();
        uploadPriority.setUploadId(uploadId);
        uploadPriority.setPriority(priority);
        uploadPriority.setType(type);
        uploadPriority.setTimestamp(System.currentTimeMillis());
        return uploadPriority;
    }

    public static UploadPriority userDefined(String uploadId, int priority) {
        return of(uploadId, priority, PriorityType.USER_DEFINED);
    }

    public static UploadPriority sizeBased(String uploadId, int priority) {
        return of(uploadId, priority, PriorityType.SIZE_BASED);
    }

    public static UploadPriority typeBased(String uploadId, int priority) {
        return of(uploadId, priority, PriorityType.TYPE_BASED);
    }

    public static UploadPriority systemAuto(String uploadId, int priority) {
        return of(uploadId, priority, PriorityType.SYSTEM_AUTO);
    }
}