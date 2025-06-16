package com.example.portal.service;

import com.example.portal.dto.UploadSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

@Service
@RequiredArgsConstructor
public class UploadScheduleService {
    private final Map<String, UploadSchedule> scheduleMap = new ConcurrentHashMap<>();
    private final PriorityBlockingQueue<UploadSchedule> scheduleQueue = new PriorityBlockingQueue<>(
            100, Comparator.comparing(UploadSchedule::getScheduledTime));
    private final FileUploadProgressService progressService;
    private final FileUploadRetryService retryService;
    private final UploadPriorityService priorityService;

    public UploadSchedule scheduleUpload(UploadSchedule schedule) {
        scheduleMap.put(schedule.getScheduleId(), schedule);
        scheduleQueue.offer(schedule);
        return schedule;
    }

    public UploadSchedule getSchedule(String scheduleId) {
        return scheduleMap.get(scheduleId);
    }

    public void cancelSchedule(String scheduleId) {
        UploadSchedule schedule = scheduleMap.remove(scheduleId);
        if (schedule != null) {
            scheduleQueue.remove(schedule);
            schedule.updateStatus("CANCELLED");
        }
    }

    public List<UploadSchedule> getSchedulesByStatus(String status) {
        return scheduleMap.values().stream()
                .filter(s -> s.getStatus().equals(status))
                .sorted(Comparator.comparing(UploadSchedule::getScheduledTime))
                .toList();
    }

    public List<UploadSchedule> getSchedulesByType(UploadSchedule.ScheduleType type) {
        return scheduleMap.values().stream()
                .filter(s -> s.getType() == type)
                .sorted(Comparator.comparing(UploadSchedule::getScheduledTime))
                .toList();
    }

    @Scheduled(fixedRate = 1000)
    public void processScheduledUploads() {
        LocalDateTime now = LocalDateTime.now();
        List<UploadSchedule> readySchedules = new ArrayList<>();

        // 실행할 예약 수집
        while (!scheduleQueue.isEmpty()) {
            UploadSchedule schedule = scheduleQueue.peek();
            if (schedule != null && !schedule.getScheduledTime().isAfter(now)) {
                readySchedules.add(scheduleQueue.poll());
            } else {
                break;
            }
        }

        // 예약된 업로드 처리
        for (UploadSchedule schedule : readySchedules) {
            try {
                processSchedule(schedule);
            } catch (Exception e) {
                handleScheduleError(schedule, e);
            }
        }
    }

    private void processSchedule(UploadSchedule schedule) {
        switch (schedule.getType()) {
            case TIME_BASED:
                processTimeBasedSchedule(schedule);
                break;
            case CONDITION_BASED:
                processConditionBasedSchedule(schedule);
                break;
            case RETRY_BASED:
                processRetryBasedSchedule(schedule);
                break;
        }
    }

    private void processTimeBasedSchedule(UploadSchedule schedule) {
        schedule.updateStatus("PROCESSING");
        // 시간 기반 예약 처리 로직
        if (progressService.isCancelled(schedule.getUploadId())) {
            schedule.updateStatus("CANCELLED");
        } else {
            schedule.updateStatus("COMPLETED");
        }
    }

    private void processConditionBasedSchedule(UploadSchedule schedule) {
        schedule.updateStatus("PROCESSING");
        // 조건 기반 예약 처리 로직
        Map<String, Object> conditions = schedule.getConditions();
        if (conditions != null) {
            // 조건 검사 및 처리
            boolean conditionsMet = checkConditions(conditions);
            if (conditionsMet) {
                schedule.updateStatus("COMPLETED");
            } else {
                schedule.updateStatus("PENDING");
                scheduleQueue.offer(schedule);
            }
        }
    }

    private void processRetryBasedSchedule(UploadSchedule schedule) {
        schedule.updateStatus("PROCESSING");
        // 재시도 기반 예약 처리 로직
        if (retryService.canRetry(schedule.getUploadId())) {
            schedule.incrementRetryCount();
            schedule.updateStatus("COMPLETED");
        } else {
            schedule.setError("Max retries exceeded");
        }
    }

    private boolean checkConditions(Map<String, Object> conditions) {
        // 조건 검사 로직 구현
        return true; // 임시 구현
    }

    private void handleScheduleError(UploadSchedule schedule, Exception e) {
        schedule.setError(e.getMessage());
        if (schedule.getType() == UploadSchedule.ScheduleType.RETRY_BASED) {
            schedule.incrementRetryCount();
            if (schedule.getRetryCount() < 3) { // 최대 3번 재시도
                schedule.updateStatus("PENDING");
                scheduleQueue.offer(schedule);
            }
        }
    }

    public void clearSchedules() {
        scheduleMap.clear();
        scheduleQueue.clear();
    }

    public List<UploadSchedule> getUpcomingSchedules() {
        return scheduleMap.values().stream()
                .filter(s -> s.getStatus().equals("PENDING"))
                .sorted(Comparator.comparing(UploadSchedule::getScheduledTime))
                .toList();
    }
}