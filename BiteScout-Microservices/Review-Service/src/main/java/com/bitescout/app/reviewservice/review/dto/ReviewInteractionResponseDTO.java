package com.bitescout.app.reviewservice.review.dto;

import com.bitescout.app.reviewservice.review.ReviewInteraction;

import java.util.List;

public record ReviewInteractionResponseDTO(
        Integer likeCount,
        List<ReviewInteraction> replies
) {


}
