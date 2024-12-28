package com.bitescout.app.rankingservice.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "restaurant-service",
        url ="${application.config.restaurants-url}"
)
@Component
public interface RestaurantClient {
    @GetMapping("/{restaurantId}")
    ResponseEntity<RestaurantDto> getRestaurantById(@PathVariable("restaurantId") UUID restaurantId);

    @GetMapping
    ResponseEntity<List<RestaurantDto>> getRestaurants();

}
