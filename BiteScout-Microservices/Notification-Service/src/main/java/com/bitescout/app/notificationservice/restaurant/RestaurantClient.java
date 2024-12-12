package com.bitescout.app.notificationservice.restaurant;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;
import java.util.List;

@FeignClient(
        name = "restaurant-service",
        url ="${application.config.restaurants-url}"
)
public interface RestaurantClient {

    //get restaurant information from restaurant service (restaurant name needed)
    @GetMapping("/{restaurant-id}")
    Optional<RestaurantResponse> getRestaurant(@PathVariable("restaurant-id") String restaurantId);

}
