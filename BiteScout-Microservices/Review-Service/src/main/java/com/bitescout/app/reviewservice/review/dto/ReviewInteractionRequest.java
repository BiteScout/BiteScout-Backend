package com.bitescout.app.reviewservice.review.dto;

import com.bitescout.app.reviewservice.review.InteractionType;

public record ReviewInteractionRequest(
        Long reviewId,
        String userId,
        InteractionType interactionType,
        String replyText
) {
}
