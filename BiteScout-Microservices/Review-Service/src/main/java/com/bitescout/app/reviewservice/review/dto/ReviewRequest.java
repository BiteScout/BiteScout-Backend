package com.bitescout.app.reviewservice.review.dto;

public record ReviewRequest (
    Long restaurantId,
    Integer rating,
    String comment
){
}
