package com.bitescout.app.notificationservice.notification;

import org.springframework.stereotype.Service;

@Service
public class NotificationMapper {

    public Notification toNotification(NotificationRequest request, Long userId){
        return Notification.builder()
                .userId(userId)
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
