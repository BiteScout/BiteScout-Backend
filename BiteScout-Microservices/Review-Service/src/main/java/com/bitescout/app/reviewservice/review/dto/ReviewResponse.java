package com.bitescout.app.reviewservice.review.dto;

import java.time.LocalDateTime;


public record ReviewResponse (
        Long id,
        Long restaurantId,
        Long customerId,
        Integer rating,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){
}
