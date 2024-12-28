package com.bitescout.app.reservationservice.restaurant;

import java.time.LocalDateTime;

public record RestaurantResponse(
        String id,
        String ownerId,
        String name

) {
}
