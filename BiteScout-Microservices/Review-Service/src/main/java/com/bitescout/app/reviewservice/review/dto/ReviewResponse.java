package com.bitescout.app.reviewservice.review.dto;

import java.time.LocalDateTime;


public record ReviewResponse (
        String id,
        String restaurantId,
        String customerId,
        Integer rating,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){
}
