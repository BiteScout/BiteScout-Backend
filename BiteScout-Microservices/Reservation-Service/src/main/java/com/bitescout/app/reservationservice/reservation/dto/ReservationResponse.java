package com.bitescout.app.reservationservice.reservation.dto;

import com.bitescout.app.reservationservice.reservation.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponse(
    Long id,
    Long customerId,
    Long restaurantId,
    LocalDateTime reservationTime,
    ReservationStatus reservationStatus,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
