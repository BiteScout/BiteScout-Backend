package com.bitescout.app.notificationservice.kafka.offer;

import org.apache.kafka.common.protocol.types.Field;

import java.time.LocalDateTime;

public record SpecialOfferMessage(
        Long id,
        String restaurantId,
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        LocalDateTime createdAt
) {


}
