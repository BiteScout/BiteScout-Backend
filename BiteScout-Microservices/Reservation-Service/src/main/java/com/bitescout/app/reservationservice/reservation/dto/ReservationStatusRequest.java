package com.bitescout.app.reservationservice.reservation.dto;

import com.bitescout.app.reservationservice.reservation.ReservationStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record ReservationStatusRequest(
        @Enumerated(value = EnumType.STRING)
        ReservationStatus reservationStatus
) {
}
