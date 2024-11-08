package com.bitescout.app.reservationservice.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCustomerId(Long customerId);
    List<Reservation> findByRestaurantId(Long restaurantId);
    Optional<Reservation> findByIdAndCustomerId(Long id, Long customerId);
}
