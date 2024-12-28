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
@RequestMapping("/v1/reservations")
public class ReservationController {

    private final ReservationService service;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody @Valid ReservationRequest request,
            @RequestAttribute("userId") String userId
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createReservation(request, userId));
    }

    @GetMapping("/users")
    public ResponseEntity<List<ReservationResponse>> getAllReservationsForUser(
            @RequestAttribute("userId") String userId
    ){
        return ResponseEntity.ok(service.getAllReservationsForUser(userId));
    }

    //need to know if restaurant-id belongs to the person who sent this request
    //check in the filter chain the person who sent this is the restaurant owner
    @GetMapping("/restaurants/{restaurant-id}")
    public ResponseEntity<List<ReservationResponse>> getAllReservationsForRestaurant(
            @PathVariable("restaurant-id") String restaurantId
    ){
        return ResponseEntity.ok(service.getAllReservationsForRestaurant(restaurantId));
    }

    //need to know if restaurant-id belongs to the person who sent this request
    //check in the filter chain the person who sent this is the restaurant owner
    @PutMapping("/{reservation-id}")
    public ResponseEntity<ReservationResponse> acceptOrDenyReservation(
            @RequestBody ReservationStatusRequest request,
            @PathVariable("reservation-id") Long reservationId
    ){
        return ResponseEntity.ok(service.acceptOrDenyReservation(reservationId, request));
    }

    //check in the filter chain the person who sent this is the reservation owner
    @DeleteMapping("/{reservation-id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteReservation(
            @PathVariable("reservation-id") Long reservationId,
            @RequestAttribute("userId") String userId
    )
    {
        service.deleteReservation(reservationId, userId);
    }

}
