package com.bitescout.app.reservationservice.restaurant;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@FeignClient(
        name = "restaurant-service",
        url ="${application.config.restaurants-url}"
)
public interface RestaurantClient {

    //get restaurant information from restaurant service (restaurant name needed)
    @GetMapping("/{restaurant-id}")
    RestaurantResponse getRestaurant(@PathVariable("restaurant-id") String restaurantId);

    @GetMapping("/owner/{ownerId}")
    List<RestaurantDTO> getRestaurantByOwnerId(@PathVariable("ownerId") String ownerId);

}
