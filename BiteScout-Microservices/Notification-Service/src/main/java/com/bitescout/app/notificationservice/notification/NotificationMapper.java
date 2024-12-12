package com.bitescout.app.notificationservice.notification;

import com.bitescout.app.notificationservice.notification.dto.NotificationRequest;
import com.bitescout.app.notificationservice.notification.dto.NotificationResponse;
import org.springframework.stereotype.Service;

@Service
public class NotificationMapper {

    public Notification toNotification(NotificationRequest request, String userId){
        return Notification.builder()
                .userId(String.valueOf(userId))
                .message(request.message())
                .notificationType(request.notificationType())
                .build();
    }

    public NotificationResponse toNotificationResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getUserId(),
                notification.getMessage(),
                notification.getNotificationType(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
