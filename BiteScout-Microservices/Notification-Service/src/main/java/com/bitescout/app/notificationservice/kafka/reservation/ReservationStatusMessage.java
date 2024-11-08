package com.bitescout.app.notificationservice.kafka.reservation;

import java.time.LocalDateTime;

public record ReservationStatusMessage(
        Long id,
        Long customerId,
        Long restaurantId,
        LocalDateTime reservationTime,
        ReservationStatus reservationStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
