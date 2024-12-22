package com.bitescout.app.reviewservice.review.dto;

public record ReviewRequest (
    String restaurantId,
    Integer rating,
    String comment
){
}
