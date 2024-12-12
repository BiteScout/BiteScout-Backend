package com.bitescout.app.notificationservice.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@FeignClient(
        name = "user-service",
        url = "${application.config.users-url}"
)
public interface UserClient {

    @GetMapping("/get-users-by-favorited-restaurant/{restaurant-id}")
    public List<UserResponse> getUsersByFavoritedRestaurant(@PathVariable("restaurant-id") String restaurantId);

    @GetMapping("/{user-id}")
    public Optional<UserResponse> getUser(@PathVariable("user-id") String userId);
}
