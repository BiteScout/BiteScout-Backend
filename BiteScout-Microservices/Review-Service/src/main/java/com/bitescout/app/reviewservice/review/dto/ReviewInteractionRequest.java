package com.bitescout.app.reviewservice.review.dto;

import com.bitescout.app.reviewservice.review.InteractionType;

public record ReviewInteractionRequest(
        String reviewId,
        InteractionType interactionType,
        String replyText
) {
}
