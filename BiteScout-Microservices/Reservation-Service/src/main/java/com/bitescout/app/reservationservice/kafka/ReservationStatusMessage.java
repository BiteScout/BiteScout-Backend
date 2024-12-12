package com.bitescout.app.reservationservice.kafka;

import com.bitescout.app.reservationservice.reservation.ReservationStatus;

import java.time.LocalDateTime;

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
