package com.bitescout.app.notificationservice.notification;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotificationRequest(
        @Size(min = 5, max = 500, message = "Message length must be between 5 and 500 characters")
        String message,
        @NotNull(message = "Notification type must be specified")
        NotificationType notificationType
) {
}
