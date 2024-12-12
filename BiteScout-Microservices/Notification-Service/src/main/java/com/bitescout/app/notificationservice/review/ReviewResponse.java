package com.bitescout.app.notificationservice.review;

import java.time.LocalDateTime;

public record ReviewResponse (
        Long id,
        String restaurantId,
        String customerId,
        Integer rating,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){
}
