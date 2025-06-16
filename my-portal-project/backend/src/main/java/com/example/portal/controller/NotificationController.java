package com.example.portal.controller;

import com.example.portal.dto.notification.NotificationDto;
import com.example.portal.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "알림 관련 API")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @Operation(summary = "알림 생성", description = "새로운 알림을 생성합니다.")
    public ResponseEntity<NotificationDto> createNotification(@RequestBody NotificationDto notificationDto) {
        return ResponseEntity.ok(notificationService.createNotification(notificationDto));
    }

    @GetMapping
    @Operation(summary = "사용자의 알림 목록 조회", description = "현재 로그인한 사용자의 모든 알림을 조회합니다.")
    public ResponseEntity<List<NotificationDto>> getNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        // TODO: UserDetails에서 userId 추출 로직 구현 필요
        Long userId = 1L; // 임시 값
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
    }

    @GetMapping("/unread")
    @Operation(summary = "읽지 않은 알림 목록 조회", description = "현재 로그인한 사용자의 읽지 않은 알림을 조회합니다.")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        // TODO: UserDetails에서 userId 추출 로직 구현 필요
        Long userId = 1L; // 임시 값
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByUserId(userId));
    }

    @PutMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 처리합니다.")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다.")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok().build();
    }
}