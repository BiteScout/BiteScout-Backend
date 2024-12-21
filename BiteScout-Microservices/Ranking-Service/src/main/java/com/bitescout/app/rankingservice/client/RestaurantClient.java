package com.bitescout.app.rankingservice.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "restaurant-service", path = "/v1/restaurant")
@Component
public interface RestaurantClient {
    @GetMapping("/restaurants/{restaurantId}")
    ResponseEntity<RestaurantDto> getRestaurantById(@PathVariable("restaurantId") UUID restaurantId);

}
