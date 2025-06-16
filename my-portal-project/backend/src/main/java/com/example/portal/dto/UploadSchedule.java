package com.example.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadSchedule {
    private Long scheduleId;
    private String uploadId;
    private LocalDateTime scheduledTime;
    private String type;
    private String status;
    private int retryCount;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, Object> conditions;

    public enum ScheduleType {
        TIME_BASED, // 시간 기반 예약
        CONDITION_BASED, // 조건 기반 예약
        RETRY_BASED // 재시도 기반 예약
    }

    public static UploadSchedule of(String uploadId, LocalDateTime scheduledTime, ScheduleType type) {
        UploadSchedule schedule = new UploadSchedule();
        schedule.setScheduleId(java.util.UUID.randomUUID().toString());
        schedule.setUploadId(uploadId);
        schedule.setScheduledTime(scheduledTime);
        schedule.setType(type.name());
        schedule.setStatus("PENDING");
        schedule.setRetryCount(0);
        return schedule;
    }

    public static UploadSchedule timeBased(String uploadId, LocalDateTime scheduledTime) {
        return of(uploadId, scheduledTime, ScheduleType.TIME_BASED);
    }

    public static UploadSchedule conditionBased(String uploadId, Map<String, Object> conditions) {
        UploadSchedule schedule = of(uploadId, LocalDateTime.now(), ScheduleType.CONDITION_BASED);
        schedule.setConditions(conditions);
        return schedule;
    }

    public static UploadSchedule retryBased(String uploadId, int retryCount) {
        UploadSchedule schedule = of(uploadId, LocalDateTime.now(), ScheduleType.RETRY_BASED);
        schedule.setRetryCount(retryCount);
        return schedule;
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public void setError(String errorMessage) {
        this.errorMessage = errorMessage;
        this.status = "FAILED";
    }
}