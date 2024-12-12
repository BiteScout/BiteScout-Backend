package com.bitescout.app.notificationservice.kafka.reservation;

import java.time.LocalDateTime;
import java.util.UUID;

public record IncomingReservationMessage(
        Long id,
        String customerId,
        String restaurantId,
        LocalDateTime reservationTime,
        ReservationStatus reservationStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}