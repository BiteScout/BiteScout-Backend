package com.bitescout.app.notificationservice.kafka.reservation;

import org.apache.kafka.common.protocol.types.Field;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationStatusMessage(
        Long id,
        String customerId,
        String restaurantId,
        LocalDateTime reservationTime,
        ReservationStatus reservationStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
