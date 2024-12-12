package com.bitescout.app.notificationservice.restaurant;

import org.apache.kafka.common.protocol.types.Field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RestaurantResponse(
        String id,
        String ownerId,
        String name,
        String description,
        String menu,
        String cuisineType,
        String location,
        String priceRange,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
