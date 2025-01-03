package com.bitescout.app.notificationservice.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/getUsersByFavRestaurant/{restaurantId}")
    public List<UserResponse> getUsersByFavoritedRestaurant(@PathVariable("restaurantId") String restaurantId);

    @GetMapping("/{userId}")
    public Optional<UserResponse> getUser(@PathVariable("userId") String userId);

    @GetMapping("/getUserByUsername/{username}")
    public Optional<UserAuthDTO> getUserByUsername(@PathVariable String username);
}
