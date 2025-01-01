package com.bitescout.app.reservationservice.reservation;

import com.bitescout.app.reservationservice.exception.InvalidStatusRequestException;
import com.bitescout.app.reservationservice.exception.ReservationNotFoundException;
import com.bitescout.app.reservationservice.kafka.ReservationProducer;
import com.bitescout.app.reservationservice.reservation.dto.ReservationRequest;
import com.bitescout.app.reservationservice.reservation.dto.ReservationResponse;
import com.bitescout.app.reservationservice.reservation.dto.ReservationStatusRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository repository;
    private final ReservationProducer producer;
    private final ReservationMapper mapper;

    public ReservationResponse createReservation(ReservationRequest request, String userId) {
        var reservation = mapper.toReservation(request, userId);
        repository.save(reservation);
        producer.sendIncomingReservationNotification(mapper.toReservationMessage(reservation));
        return mapper.toReservationResponse(reservation);
    }

    public List<ReservationResponse> getAllReservationsForUser(String userId) {
        return repository.findByCustomerId(userId)
                .stream()
                .map(mapper::toReservationResponse)
                .sorted(Comparator.comparing(ReservationResponse::reservationTime).reversed())
                .collect(Collectors.toList());
    }


    public List<ReservationResponse> getAllReservationsForRestaurant(String restaurantId) {
        return repository.findByRestaurantId(restaurantId)
                .stream()
                .map(mapper::toReservationResponse)
                .sorted(Comparator.comparing(ReservationResponse::reservationTime).reversed())
                .collect(Collectors.toList());
    }

    // can implement: can change acceptance or rejection for 5 minutes(time window to correct if it was a mistake),
    // after that it becomes locked.
    public ReservationResponse acceptOrDenyReservation(Long id, ReservationStatusRequest request) {
        var reservation = repository.findById(id).orElseThrow(()->
                new ReservationNotFoundException("This reservation was not found"));
        var currentStatus = reservation.getReservationStatus();
        if(currentStatus != ReservationStatus.ON_HOLD){
            throw new InvalidStatusRequestException("This reservation's status is no longer ON_HOLD, thus can not be" +
                    " updated");
        }
        var statusRequest = request.reservationStatus();
        if(statusRequest == ReservationStatus.ON_HOLD){
            throw new InvalidStatusRequestException("Status field must be updated with either REJECTED or ACCEPTED");
        }

        reservation.setReservationStatus(statusRequest);
        repository.save(reservation);

        producer.sendReservationNotification(mapper.toReservationMessage(reservation));

        return mapper.toReservationResponse(reservation);
    }

    public void deleteReservation(Long reservationId, String userId) {
        var reservation = repository.findByIdAndCustomerId(reservationId, userId).orElseThrow(()->
                new ReservationNotFoundException(String.format("Reservation with id %d and user id %s not found",
                        reservationId, userId)));
        repository.deleteById(reservationId);
    }

    public void deleteReservationOwner(Long reservationId, String ownerId) {
        var reservation = repository.findById(reservationId).orElseThrow(()->
                new ReservationNotFoundException(String.format("Reservation with id %d not found", reservationId)));
        repository.deleteById(reservationId);
    }
}
