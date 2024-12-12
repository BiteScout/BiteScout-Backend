package com.bitescout.app.reservationservice.reservation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservationRequest(
        @NotNull(message = "Restaurant id must be provided")
        String restaurantId,
        @NotNull(message = "Reservation time must be provided")
        @Future(message = "Reservation date must be in the future")
        LocalDateTime reservationTime
) {
}
