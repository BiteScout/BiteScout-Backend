package com.bitescout.app.notificationservice.notification.dto;

import com.bitescout.app.notificationservice.notification.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
    Long id,
    String userId,
    String message,
    NotificationType notificationType,
    boolean isRead,
    LocalDateTime createdAt
) {
}
