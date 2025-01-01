package com.bitescout.app.restaurantservice.client;

import com.bitescout.app.restaurantservice.dto.UserDTO;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "user-service", path = "v1/users")
public interface UserServiceClient {
    @GetMapping("/{userId}")
    ResponseEntity<UserDTO> getUser(@PathVariable String userId);

    @GetMapping("/isEnabled/{userId}")
    ResponseEntity<Boolean> isEnabled(@PathVariable String userId);
}
