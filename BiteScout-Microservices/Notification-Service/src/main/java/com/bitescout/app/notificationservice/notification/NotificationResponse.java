package com.bitescout.app.notificationservice.notification;

import org.apache.kafka.common.protocol.types.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record NotificationResponse(
    Long id,
    Long userId,
    String message,
    NotificationType notificationType,
    boolean isRead,
    LocalDateTime createdAt
) {
}
