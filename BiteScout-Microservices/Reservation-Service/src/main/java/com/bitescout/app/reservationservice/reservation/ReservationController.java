package com.bitescout.app.reservationservice.reservation;

import com.bitescout.app.reservationservice.reservation.dto.ReservationRequest;
import com.bitescout.app.reservationservice.reservation.dto.ReservationResponse;
import com.bitescout.app.reservationservice.reservation.dto.ReservationStatusRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reservations")
public class ReservationController {

    private final ReservationService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwner(#userId, principal)")
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody @Valid ReservationRequest request,
            @RequestHeader("User-Id") String userId
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createReservation(request, userId));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwner(#userId, principal)")
    public ResponseEntity<List<ReservationResponse>> getAllReservationsForUser(
            @RequestHeader("User-Id") String userId
    ){
        return ResponseEntity.ok(service.getAllReservationsForUser(userId));
    }

    //need to know if restaurant-id belongs to the person who sent this request
    //check in the filter chain the person who sent this is the restaurant owner
    @PreAuthorize("hasRole('ADMIN') or ((@securityService.isRestaurantOwner(#restaurantId, principal) and hasRole('RESTAURANT_OWNER'))")
    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<List<ReservationResponse>> getAllReservationsForRestaurant(
            @PathVariable("restaurantId") String restaurantId
    ){
        return ResponseEntity.ok(service.getAllReservationsForRestaurant(restaurantId));
    }
    //need to know if restaurant-id belongs to the person who sent this request
    //check in the filter chain the person who sent this is the restaurant owner
    @PutMapping("/{reservation-id}")
    @PreAuthorize("hasRole('ADMIN') or (@securityService.isRestaurantOwnerReservations(#reservationId, principal) and hasRole('RESTAURANT_OWNER'))")
    public ResponseEntity<ReservationResponse> acceptOrDenyReservation(
            @RequestBody ReservationStatusRequest request,
            @PathVariable("reservation-id") Long reservationId
    ){
        return ResponseEntity.ok(service.acceptOrDenyReservation(reservationId, request));
    }

    //check in the filter chain the person who sent this is the reservation owner
    @DeleteMapping("/{reservation-id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or (@securityService.isRestaurantOwnerReservations(#reservationId, principal) and hasRole('RESTAURANT_OWNER'))")
    public void deleteReservation(
            @PathVariable("reservation-id") Long reservationId,
            @RequestHeader(value = "User-Id") String userId
    )
    {
        service.deleteReservationOwner(reservationId, userId);
    }

    @DeleteMapping("user/{reservation-id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @securityService.isReservationUser(#reservationId, principal)")
    public void deleteReservationUser(
            @PathVariable("reservation-id") Long reservationId,
            @RequestHeader(value = "User-Id") String userId
    )
    {
        service.deleteReservation(reservationId, userId);
    }

}
