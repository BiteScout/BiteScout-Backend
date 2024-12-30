package com.bitescout.app.reviewservice.review.dto;

public record ReviewUpdateRequest (
        Integer rating,
        String comment
){}
