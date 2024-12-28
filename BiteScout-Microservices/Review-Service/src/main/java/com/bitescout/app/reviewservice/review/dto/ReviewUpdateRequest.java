package com.bitescout.app.reviewservice.review.dto;

public record ReviewUpdateRequest (
        String reviewId,
        Integer rating,
        String comment
){}
