package com.bitescout.app.notificationservice.kafka.review;


import java.time.LocalDateTime;

public record ReviewInteractionMessage(
        Long id,
        Long reviewId,
        Long userId,        // user id = id of the person who posted the comment/like/dislike, the interacting person
        InteractionType interactionType,
        String replyText,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){
}


