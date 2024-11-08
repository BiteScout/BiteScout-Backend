package com.bitescout.app.notificationservice.notification.dto;

import com.bitescout.app.notificationservice.notification.NotificationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotificationRequest(
        @Size(min = 5, max = 500, message = "Message length must be between 5 and 500 characters")
        @NotNull(message = "Message must not be null")
        String message,
        @NotNull(message = "Notification type must be specified")
        NotificationType notificationType
) {
}
