package com.bitescout.app.reservationservice.reservation.dto;

import com.bitescout.app.reservationservice.reservation.ReservationStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;

public record ReservationStatusRequest(
        @Enumerated(value = EnumType.STRING)
        @NotBlank(message = "reservation status can not be blank")
        ReservationStatus reservationStatus
) {
}
