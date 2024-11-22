package com.bitescout.app.notificationservice.kafka.reservation;

import java.time.LocalDateTime;

public record IncomingReservationMessage(
        Long id,
        Long customerId,
        Long restaurantId,
        LocalDateTime reservationTime,
        LocalDateTime createdAt
) {
}