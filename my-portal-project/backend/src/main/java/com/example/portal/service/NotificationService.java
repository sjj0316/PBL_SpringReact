package com.example.portal.service;

import com.example.portal.dto.notification.NotificationDto;
import java.util.List;

public interface NotificationService {
    NotificationDto createNotification(NotificationDto notificationDto);

    List<NotificationDto> getNotificationsByUserId(Long userId);

    void markNotificationAsRead(Long notificationId);

    void deleteNotification(Long notificationId);

    List<NotificationDto> getUnreadNotificationsByUserId(Long userId);
}