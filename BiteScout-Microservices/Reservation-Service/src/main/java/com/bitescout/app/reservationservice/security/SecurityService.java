package com.bitescout.app.reservationservice.security;


import com.bitescout.app.reservationservice.reservation.Reservation;
import com.bitescout.app.reservationservice.reservation.ReservationRepository;
import com.bitescout.app.reservationservice.restaurant.RestaurantClient;
import com.bitescout.app.reservationservice.restaurant.RestaurantDTO;
import com.bitescout.app.reservationservice.restaurant.RestaurantResponse;
import com.bitescout.app.reservationservice.user.UserClient;
import com.bitescout.app.reservationservice.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private static final Logger log = LoggerFactory.getLogger(SecurityService.class);
    private final UserClient userClient;
    private final RestaurantClient restaurantClient;
    private final ReservationRepository reservationRepository;

    public boolean isOwner(String ownerId, String principal) {
        UserResponse user = userClient.getUser(ownerId).get();
        if (user != null) {
            System.out.println("Owner ID: " + ownerId);
            System.out.println("Principal: " + principal);
            System.out.println("Owner Username: " + user.username());
            return user.username().equals(principal);
        }
        log.info("Owner ID: {}, Principal: {}", ownerId, principal);

        return false;
    }
    public boolean isRestaurantOwner(String restaurantId, String principal) {
        UserResponse user = userClient.getUserByUsername(principal).orElse(null);
        if (user == null) {
            log.warn("User not found for principal: {}", principal);
            return false;
        }

        List<RestaurantDTO> restaurants = restaurantClient.getRestaurantByOwnerId(user.id());
        if (restaurants == null || restaurants.isEmpty()) {
            log.warn("No restaurants found for owner ID: {}", user.id());
            return false;
        }

        for (RestaurantDTO restaurant : restaurants) {
            log.info("Checking Restaurant ID: {} (length: {}) against provided ID: {} (length: {})",
                    restaurant.getId(), restaurant.getId().length(), restaurantId, restaurantId.length());
            if (restaurant.getId().trim().equalsIgnoreCase(restaurantId.trim())) {
                log.info("Match found for Restaurant ID: {}", restaurant.getId());
                return true;
            }
        }

        log.info("Principal: {}, Is Owner: false", principal);
        return false;
    }



    public boolean isRestaurantOwnerReservations(Long reservationId, String principal) {
        // Fetch user based on principal
        UserResponse user = userClient.getUserByUsername(principal)
                .orElseThrow(() -> new IllegalArgumentException("User not found for principal: " + principal));
        // Fetch reservation based on ID
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found for ID: " + reservationId));

        // Fetch restaurants for the reservation and the user's ownership
        RestaurantResponse reservationRestaurants = restaurantClient.getRestaurant(reservation.getRestaurantId());
        List<RestaurantDTO> userRestaurants = restaurantClient.getRestaurantByOwnerId(user.id());

        // Debugging logs
        log.info("Reservation Restaurants: {}", reservationRestaurants);
        log.info("User Restaurants: {}", userRestaurants);

        // Check if the user is the owner of the restaurant
        for (RestaurantDTO restaurant : userRestaurants) {
            if (restaurant.getId().trim().equalsIgnoreCase(reservationRestaurants.id().trim())) {
                log.info("Match found for Restaurant ID: {}", restaurant.getId());
                return true;
            }
        }

        return false;
    }

    public boolean isReservationUser(Long reservationId, String principal) {
        // Fetch user based on principal
        UserResponse user = userClient.getUserByUsername(principal)
                .orElseThrow(() -> new IllegalArgumentException("User not found for principal: " + principal));
        // Fetch reservation based on ID
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found for ID: " + reservationId));

        // Check if the user is the owner of the reservation
        return reservation.getCustomerId().trim().equalsIgnoreCase(user.id().trim());
    }



}