package com.bitescout.app.reservationservice.reservation;

import com.bitescout.app.reservationservice.reservation.dto.ReservationRequest;
import com.bitescout.app.reservationservice.reservation.dto.ReservationResponse;
import com.bitescout.app.reservationservice.reservation.dto.ReservationStatusRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService service;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody @Valid ReservationRequest request,
            @RequestHeader("User-Id") Long userId
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createReservation(request, userId));
    }

    @GetMapping("/users")
    public ResponseEntity<List<ReservationResponse>> getAllReservationsForUser(
            @RequestHeader("User-Id") Long userId
    ){
        return ResponseEntity.ok(service.getAllReservationsForUser(userId));
    }

    //for the next two: basically if request requires the sender is a restaurant owner, check it in restaurant service

    //reject this request from filter chain if user doesn't have restaurant owner role
    //in restaurant service check if the user sending this request is owner of this restaurant
    @GetMapping("/restaurants/{restaurant-id}")
    public ResponseEntity<List<ReservationResponse>> getAllReservationsForRestaurant(
            @PathVariable("restaurant-id") Long restaurantId
    ){
        return ResponseEntity.ok(service.getAllReservationsForRestaurant(restaurantId));
    }

    //reject this request from filter chain if user doesn't have restaurant owner role
    //in restaurant service check if the user sending this request is owner of this restaurant
    @PutMapping("/{reservation-id}")
    public ResponseEntity<ReservationResponse> acceptOrDenyReservation(
            @RequestBody ReservationStatusRequest request,
            @PathVariable("reservation-id") Long reservationId
    ){

        return ResponseEntity.ok(service.acceptOrDenyReservation(reservationId, request));
    }

    @DeleteMapping("/{reservation-id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteReservation(
            @PathVariable("reservation-id") Long reservationId,
            @RequestHeader(value = "User-Id") Long userId
    )
    {
        service.deleteReservation(reservationId, userId);
    }

}
