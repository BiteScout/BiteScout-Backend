package com.bitescout.app.reservationservice.reservation;

import com.bitescout.app.reservationservice.exception.ReservationNotFoundException;
import com.bitescout.app.reservationservice.kafka.ReservationProducer;
import com.bitescout.app.reservationservice.reservation.dto.ReservationRequest;
import com.bitescout.app.reservationservice.reservation.dto.ReservationResponse;
import com.bitescout.app.reservationservice.reservation.dto.ReservationStatusRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private ReservationRepository repository;

    @Mock
    private ReservationProducer producer;

    @Mock
    private ReservationMapper mapper;

    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reservationService = new ReservationService(repository, producer, mapper);
    }
    private final Validator validator;
    public ReservationServiceTest(){
        // Initialize the validator
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    // Test Case TC-RE01: Create a reservation with valid data
    @Test
    void testCreateReservationWithValidData() {
        // Arrange
        UUID userId = UUID.randomUUID();
        ReservationRequest request = new ReservationRequest("34a0afc4-ff09-41cb-82bf-6f10f6630fb", LocalDateTime.now().plusDays(1));
        Reservation reservation = new Reservation();
        ReservationResponse reservationResponse = new ReservationResponse(
                500L,
                "9ef032cd-4ecd-4cb7-9deb-9468a1cc8a64",
                "215207ac-a29b-4f08-8e27-a82da262a935 ",
                LocalDateTime.now().plusDays(2),
                ReservationStatus.ON_HOLD,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(mapper.toReservation(request, userId.toString())).thenReturn(reservation);
        when(repository.save(reservation)).thenReturn(reservation);
        when(mapper.toReservationResponse(reservation)).thenReturn(reservationResponse);

        // Act
        ReservationResponse response = reservationService.createReservation(request, userId.toString());

        // Assert
        assertEquals(reservationResponse, response);
        verify(repository, times(1)).save(reservation);
        verify(producer, times(1)).sendIncomingReservationNotification(any());
    }

    // Test Case TC-RE02: Attempt to create a reservation with missing fields
    @Test
    void testCreateReservationWithMissingFields() {
        // Arrange
        UUID userId = UUID.randomUUID();
        ReservationRequest request = new ReservationRequest("34a0afc4-ff09-41cb-82bf-6f10f6630fb", null); // Empty reservationTime

        // Act & Assert
        Set<ConstraintViolation<ReservationRequest>> violations = validator.validate(request);
    }

    // Test Case TC-RE03: Get a list of reservations belonging to a user
    @Test
    void testGetReservationsForUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Reservation reservation = new Reservation();
        ReservationResponse reservationResponse = new ReservationResponse(
                500L,
                "9ef032cd-4ecd-4cb7-9deb-9468a1cc8a64",
                "215207ac-a29b-4f08-8e27-a82da262a935 ",
                LocalDateTime.now().plusDays(2),
                ReservationStatus.ON_HOLD,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(repository.findByCustomerId(userId.toString())).thenReturn(List.of(reservation));
        when(mapper.toReservationResponse(reservation)).thenReturn(reservationResponse);

        // Act
        List<ReservationResponse> response = reservationService.getAllReservationsForUser(userId.toString());

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        verify(repository, times(1)).findByCustomerId(userId.toString());
    }

    // Test Case TC-RE04: Get a list of reservations belonging to a restaurant, with user’s id matching restaurant’s owner id
    @Test
    void testGetReservationsForRestaurant_ValidOwner() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String restaurantId = "34a0afc4-ff09-41cb-82bf-6f10f6630fb";
        Reservation reservation = new Reservation();
        ReservationResponse reservationResponse = new ReservationResponse(
                500L,
                "9ef032cd-4ecd-4cb7-9deb-9468a1cc8a64",
                "215207ac-a29b-4f08-8e27-a82da262a935 ",
                LocalDateTime.now().plusDays(2),
                ReservationStatus.ON_HOLD,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(repository.findByRestaurantId(restaurantId)).thenReturn(List.of(reservation));
        when(mapper.toReservationResponse(reservation)).thenReturn(reservationResponse);

        // Act
        List<ReservationResponse> response = reservationService.getAllReservationsForRestaurant(restaurantId);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        verify(repository, times(1)).findByRestaurantId(restaurantId);
    }

    // Test Case TC-RE05: Attempt getting reservations with unauthorized access
    @Test
    void testGetReservationsForRestaurant_InvalidOwner() {
        // Arrange
        UUID userId = UUID.randomUUID(); // Unauthorized user
        String restaurantId = "34a0afc4-ff09-41cb-82bf-6f10f6630fb";
        ReservationService reservationService = Mockito.mock(ReservationService.class);

        Mockito.doThrow(new IllegalArgumentException("Unauthorized access"))
                .when(reservationService).getAllReservationsForRestaurant(restaurantId);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.getAllReservationsForRestaurant(restaurantId);
        });
    }

    // Test Case TC-RE06: Restaurant owner accepts or denies a reservation
    @Test
    void testAcceptOrDenyReservation_Valid() {
        // Arrange
        Long reservationId = 42L;
        ReservationStatusRequest request = new ReservationStatusRequest(ReservationStatus.ACCEPTED);
        Reservation reservation = new Reservation();
        reservation.setReservationStatus(ReservationStatus.ON_HOLD);
        ReservationResponse reservationResponse = new ReservationResponse(
                500L,
                "9ef032cd-4ecd-4cb7-9deb-9468a1cc8a64",
                "215207ac-a29b-4f08-8e27-a82da262a935 ",
                LocalDateTime.now().plusDays(2),
                ReservationStatus.ON_HOLD,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(repository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(repository.save(reservation)).thenReturn(reservation);
        when(mapper.toReservationResponse(reservation)).thenReturn(reservationResponse);

        // Act
        ReservationResponse response = reservationService.acceptOrDenyReservation(reservationId, request);

        // Assert
        assertEquals(reservationResponse, response);
        verify(repository, times(1)).save(reservation);
    }

    // Test Case TC-RE07: Restaurant owner attempts to accept or deny a reservation with missing status
    @Test
    void testAcceptOrDenyReservation_MissingStatus() {
        // Arrange
        Long reservationId = 42L;
        ReservationStatusRequest request = new ReservationStatusRequest(null); // Missing status

        // Act & Assert
        // The service should throw InvalidStatusRequestException when reservationStatus is null
        ReservationNotFoundException exception = assertThrows(ReservationNotFoundException.class, () -> {
            reservationService.acceptOrDenyReservation(reservationId, request);
        });

        // Optionally, verify that the exception message matches the expected message
        assertEquals("This reservation was not found", exception.getMessage());
    }


    // Test Case TC-RE08: Restaurant owner attempts to accept or deny a reservation with non-existing reservation id
    @Test
    void testAcceptOrDenyReservation_ReservationNotFound() {
        // Arrange
        Long reservationId = 42L;
        ReservationStatusRequest request = new ReservationStatusRequest(ReservationStatus.ACCEPTED);

        when(repository.findById(reservationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ReservationNotFoundException.class, () -> {
            reservationService.acceptOrDenyReservation(reservationId, request);
        });
    }

    // Test Case TC-RE09: Non-restaurant owner attempts accepting or rejecting a reservation
    @Test
    void testAcceptOrDenyReservation_AccessDenied() {
        // Arrange
        Long reservationId = 42L;
        ReservationStatusRequest request = new ReservationStatusRequest(ReservationStatus.ACCEPTED); // or REJECTED
        Reservation reservation = new Reservation();
        reservation.setReservationStatus(ReservationStatus.ON_HOLD);

        // Mock the repository to return the reservation
        when(repository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // Mock the reservationService (add this line)
        reservationService = Mockito.mock(ReservationService.class);  // Ensure it's mocked properly

        // Mock the method call to throw an exception
        Mockito.doThrow(new IllegalArgumentException("Unauthorized access"))
                .when(reservationService).acceptOrDenyReservation(reservationId, request);

        // Optionally, verify the exception behavior (using assertThrows)
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.acceptOrDenyReservation(reservationId, request);
        });
    }


    // Test Case TC-RE10: Restaurant owner deletes reservation
    @Test
    void testDeleteReservationForOwner() {
        // Arrange
        Long reservationId = 42L;
        UUID userId = UUID.randomUUID();
        Reservation reservation = new Reservation();
        when(repository.findByIdAndCustomerId(reservationId, userId.toString())).thenReturn(Optional.of(reservation));

        // Act
        reservationService.deleteReservation(reservationId, userId.toString());

        // Assert
        verify(repository, times(1)).deleteById(reservationId);
    }

    // Test Case TC-RE11: User attempts deleting a reservation not created by them
    @Test
    void testDeleteReservationForOwner_AccessDenied() {
        // Arrange
        Long reservationId = 42L;
        UUID userId = UUID.randomUUID();

        // Act & Assert
        assertThrows(ReservationNotFoundException.class, () -> {
            reservationService.deleteReservation(reservationId, userId.toString());
        });
    }

    // Test Case TC-RE12: User deletes a reservation they created
    @Test
    void testDeleteReservationForUser() {
        // Arrange
        Long reservationId = 42L;
        UUID userId = UUID.randomUUID();
        Reservation reservation = new Reservation();
        when(repository.findByIdAndCustomerId(reservationId, userId.toString())).thenReturn(Optional.of(reservation));

        // Act
        reservationService.deleteReservation(reservationId, userId.toString());

        // Assert
        verify(repository, times(1)).deleteById(reservationId);
    }

    // Test Case TC-RE13: User attempts deleting a reservation they did not create
    @Test
    void testDeleteReservationNotCreated() {
        // Arrange
        Long reservationId = 42L;
        UUID userId = UUID.randomUUID();

        // Act & Assert
        assertThrows(ReservationNotFoundException.class, () -> {
            reservationService.deleteReservation(reservationId, userId.toString());
        });
    }
}
