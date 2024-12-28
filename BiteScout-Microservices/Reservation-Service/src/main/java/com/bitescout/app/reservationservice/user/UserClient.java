package com.bitescout.app.reservationservice.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(
        name = "user-service",
        url = "${application.config.users-url}"
)
public interface UserClient {

    @GetMapping("/{user-id}")
    public Optional<UserResponse> getUser(@PathVariable("user-id") String userId);

    @GetMapping("/getUserByUsername/{username}")
    public Optional<UserAuthDTO> getUserByUsername(@PathVariable String username);
}
