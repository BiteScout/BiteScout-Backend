package com.bitescout.app.notificationservice.kafka.offer;

import java.time.LocalDate;
import java.util.UUID;


public record SpecialOfferMessage(
        UUID id,
        UUID restaurantId,
        String restaurantName,
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate
) {


}
