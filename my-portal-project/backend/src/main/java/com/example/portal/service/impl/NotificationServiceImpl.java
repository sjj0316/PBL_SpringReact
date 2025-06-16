package com.example.portal.service.impl;

import com.example.portal.dto.notification.NotificationDto;
import com.example.portal.entity.Notification;
import com.example.portal.repository.NotificationRepository;
import com.example.portal.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public NotificationDto createNotification(NotificationDto notificationDto) {
        Notification notification = Notification.builder()
                .userId(notificationDto.getUserId())
                .title(notificationDto.getTitle())
                .content(notificationDto.getContent())
                .type(notificationDto.getType())
                .read(false)
                .link(notificationDto.getLink())
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        return convertToDto(savedNotification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void markNotificationAsRead(Long notificationId) {
        notificationRepository.findById(notificationId)
                .ifPresent(notification -> {
                    notification.markAsRead();
                    notificationRepository.save(notification);
                });
    }

    @Override
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private NotificationDto convertToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .link(notification.getLink())
                .build();
    }
}