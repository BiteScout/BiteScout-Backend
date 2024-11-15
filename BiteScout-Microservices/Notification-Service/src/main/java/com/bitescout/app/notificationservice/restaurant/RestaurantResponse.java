package com.bitescout.app.notificationservice.restaurant;

import org.apache.kafka.common.protocol.types.Field;

import java.time.LocalDateTime;
import java.util.List;

//might also copy from restaurant dto in restaurant service
public record RestaurantResponse(
        Long id,
        Long ownerId,
        String name,
        String description,
        String cuisineType,
        String location,
        String priceRange,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<Long> favoritedUserIds

) {
}
