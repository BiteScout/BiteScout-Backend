package com.bitescout.app.reviewservice.review.dto;

public record ReviewRequest (
    Long restaurantId,
    int rating,
    String comment
){
}
