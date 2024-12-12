package com.bitescout.app.reservationservice.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCustomerId(String customerId);
    List<Reservation> findByRestaurantId(String restaurantId);
    Optional<Reservation> findByIdAndCustomerId(Long id, String customerId);
}
