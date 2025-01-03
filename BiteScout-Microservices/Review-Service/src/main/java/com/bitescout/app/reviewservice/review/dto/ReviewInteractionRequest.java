package com.bitescout.app.reviewservice.review.dto;

import com.bitescout.app.reviewservice.review.InteractionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewInteractionRequest(
        @NotBlank
        String reviewId,

        @NotNull
        InteractionType interactionType,

        @Size(min = 8, message = "Comment must be at least 8 characters long")
        @Size(max = 1024, message = "Comment must be at must 1024 characters long ")
        String replyText
) {
}
