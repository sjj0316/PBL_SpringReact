package com.example.portal.service;

import com.example.portal.dto.notification.NotificationDto;
import com.example.portal.entity.Notification;
import com.example.portal.repository.NotificationRepository;
import com.example.portal.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification notification;
    private NotificationDto notificationDto;

    @BeforeEach
    void setUp() {
        notification = Notification.builder()
                .id(1L)
                .userId(1L)
                .title("테스트 알림")
                .content("테스트 내용")
                .type("SYSTEM")
                .read(false)
                .build();

        notificationDto = NotificationDto.builder()
                .id(1L)
                .userId(1L)
                .title("테스트 알림")
                .content("테스트 내용")
                .type("SYSTEM")
                .read(false)
                .build();
    }

    @Test
    void createNotification_ShouldReturnCreatedNotification() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        NotificationDto result = notificationService.createNotification(notificationDto);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(notificationDto.getTitle());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void getNotificationsByUserId_ShouldReturnNotificationList() {
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(Arrays.asList(notification));

        List<NotificationDto> results = notificationService.getNotificationsByUserId(1L);

        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(1);
        verify(notificationRepository, times(1)).findByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void markNotificationAsRead_ShouldUpdateNotificationStatus() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.markNotificationAsRead(1L);

        verify(notificationRepository, times(1)).findById(1L);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
}