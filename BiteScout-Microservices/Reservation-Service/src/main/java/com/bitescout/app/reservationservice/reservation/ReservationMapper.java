package com.bitescout.app.reservationservice.reservation;

import com.bitescout.app.reservationservice.kafka.ReservationStatusMessage;
import com.bitescout.app.reservationservice.reservation.dto.ReservationRequest;
import com.bitescout.app.reservationservice.reservation.dto.ReservationResponse;
import org.springframework.stereotype.Service;

@Service
public class ReservationMapper {

    public Reservation toReservation(ReservationRequest request, Long userId){
        return Reservation.builder()
                .customerId(userId)
                .restaurantId(request.restaurantId())
                .reservationTime(request.reservationTime())
                .build();
    }

    public ReservationResponse toReservationResponse(Reservation reservation){
        return new ReservationResponse(
                reservation.getId(),
                reservation.getCustomerId(),
                reservation.getRestaurantId(),
                reservation.getReservationTime(),
                reservation.getReservationStatus(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }

    public ReservationStatusMessage toReservationMessage(Reservation reservation) {
        return new ReservationStatusMessage(
                reservation.getId(),
                reservation.getCustomerId(),
                reservation.getRestaurantId(),
                reservation.getReservationTime(),
                reservation.getReservationStatus(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }
}
