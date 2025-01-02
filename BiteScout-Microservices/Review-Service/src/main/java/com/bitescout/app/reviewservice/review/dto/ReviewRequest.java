package com.bitescout.app.reviewservice.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequest (
    @NotNull
    String restaurantId,

    @Min(value = 0, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    Integer rating,

    @Size(min = 8, message = "Comment must be at least 8 characters long")
    @Size(max = 1024, message = "Comment must be at must 1024 characters long ")
    String comment
){
}
