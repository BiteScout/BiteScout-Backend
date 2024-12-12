package com.bitescout.app.reservationservice.reservation.dto;

import com.bitescout.app.reservationservice.reservation.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponse(
    Long id,
    String customerId,
    String restaurantId,
    LocalDateTime reservationTime,
    ReservationStatus reservationStatus,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
