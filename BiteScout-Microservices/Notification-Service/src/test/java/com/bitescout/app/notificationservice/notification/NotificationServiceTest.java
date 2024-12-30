package com.bitescout.app.notificationservice.notification;

import com.bitescout.app.notificationservice.exception.NotificationNotFoundException;
import com.bitescout.app.notificationservice.notification.dto.NotificationRequest;
import com.bitescout.app.notificationservice.notification.dto.NotificationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @InjectMocks
    private NotificationService service;

    @Mock
    private NotificationRepository repository;

    @Mock
    private NotificationMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateNotification() {
        // Arrange
        String userId = "3fc8656d-3c75-43c2-952a-2fb3ca070240";
        NotificationRequest request = new NotificationRequest("Test Notification", NotificationType.RESERVATION_STATUS_NOTIFICATION);
        Notification notification = new Notification();
        NotificationResponse response = new NotificationResponse(1L,
                "3fc8656d-3c75-43c2-952a-2fb3ca070240",
                "Content",
                NotificationType.RESERVATION_STATUS_NOTIFICATION,
                false,
                LocalDateTime.now());

        when(mapper.toNotification(request, userId)).thenReturn(notification);
        when(repository.save(notification)).thenReturn(notification);
        when(mapper.toNotificationResponse(notification)).thenReturn(response);

        // Act
        NotificationResponse result = service.createNotification(request, userId);

        // Assert
        assertNotNull(result);
        assertEquals(response, result);
        verify(repository, times(1)).save(notification);
    }

    @Test
    void testGetNotifications() {
        // Arrange
        String userId = "3fc8656d-3c75-43c2-952a-2fb3ca070240";
        Notification notification = new Notification();
        NotificationResponse response = new NotificationResponse(1L,
                "3fc8656d-3c75-43c2-952a-2fb3ca070240",
                "Content",
                NotificationType.RESERVATION_STATUS_NOTIFICATION,
                false,
                LocalDateTime.now());

        when(repository.findByUserId(userId)).thenReturn(List.of(notification));
        when(mapper.toNotificationResponse(notification)).thenReturn(response);

        // Act
        List<NotificationResponse> results = service.getNotifications(userId);

        // Assert
        assertEquals(1, results.size());
        assertEquals(response, results.get(0));
    }

    @Test
    void testMarkAsSeen_Success() {
        // Arrange
        String userId = "3fc8656d-3c75-43c2-952a-2fb3ca070240";
        Long notificationId = 1L;
        Notification notification = new Notification();
        NotificationResponse response = new NotificationResponse(1L,
                "3fc8656d-3c75-43c2-952a-2fb3ca070240",
                "Content",
                NotificationType.RESERVATION_STATUS_NOTIFICATION,
                false,
                LocalDateTime.now());

        when(repository.findByIdAndUserId(notificationId, userId)).thenReturn(Optional.of(notification));
        when(repository.save(notification)).thenReturn(notification);
        when(mapper.toNotificationResponse(notification)).thenReturn(response);

        // Act
        NotificationResponse result = service.markAsSeen(notificationId, userId);

        // Assert
        assertNotNull(result);
        assertTrue(notification.isRead());
        assertEquals(response, result);
    }

    @Test
    void testMarkAsSeen_NotFound() {
        // Arrange
        String userId = "3fc8656d-3c75-43c2-952a-2fb3ca070240";
        Long notificationId = 1L;

        when(repository.findByIdAndUserId(notificationId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotificationNotFoundException.class, () -> service.markAsSeen(notificationId, userId));
    }

    @Test
    void testDeleteNotification_Success() {
        // Arrange
        String userId = "3fc8656d-3c75-43c2-952a-2fb3ca070240";
        Long notificationId = 1L;
        Notification notification = new Notification();

        when(repository.findByIdAndUserId(notificationId, userId)).thenReturn(Optional.of(notification));

        // Act
        service.deleteNotification(notificationId, userId);

        // Assert
        verify(repository, times(1)).deleteById(notificationId);
    }

    @Test
    void testDeleteNotification_NotFound() {
        // Arrange
        String userId = "3fc8656d-3c75-43c2-952a-2fb3ca070240";
        Long notificationId = 1L;

        when(repository.findByIdAndUserId(notificationId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotificationNotFoundException.class, () -> service.deleteNotification(notificationId, userId));
    }
}
