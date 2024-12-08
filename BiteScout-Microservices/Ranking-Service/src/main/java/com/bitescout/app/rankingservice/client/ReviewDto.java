package com.bitescout.app.rankingservice.client;

import java.util.UUID;

public record ReviewDto (
        UUID restaurantId,
        Integer rating
){
}