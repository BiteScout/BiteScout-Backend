package com.bitescout.app.notificationservice.notification;

import com.bitescout.app.notificationservice.notification.dto.NotificationRequest;
import com.bitescout.app.notificationservice.notification.dto.NotificationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationMapperTest {

    @InjectMocks
    private NotificationMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testToNotification() {
        // Arrange
        String userId = "3fc8656d-3c75-43c2-952a-2fb3ca070240";
        NotificationRequest request = new NotificationRequest("Test Message", NotificationType.DEFAULT);

        // Act
        Notification result = mapper.toNotification(request, userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(request.message(), result.getMessage());
        assertEquals(request.notificationType(), result.getNotificationType());
        assertFalse(result.isRead());
    }

    @Test
    void testToNotificationResponse() {
        // Arrange
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setUserId("3fc8656d-3c75-43c2-952a-2fb3ca070240");
        notification.setMessage("Test Message");
        notification.setNotificationType(NotificationType.DEFAULT);
        notification.setRead(true);
        notification.setCreatedAt(LocalDateTime.now());

        // Act
        NotificationResponse response = mapper.toNotificationResponse(notification);

        // Assert
        assertNotNull(response);
        assertEquals(notification.getId(), response.id());
        assertEquals(notification.getUserId(), response.userId());
        assertEquals(notification.getMessage(), response.message());
        assertEquals(notification.getNotificationType(), response.notificationType());
        assertEquals(notification.isRead(), response.isRead());
        assertEquals(notification.getCreatedAt(), response.createdAt());
    }
}
