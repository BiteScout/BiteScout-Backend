package com.bitescout.app.authservice.client;

import com.bitescout.app.authservice.dto.UserDto;
import com.bitescout.app.authservice.request.RegisterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", path = "/v1/users")
public interface UserServiceClient {
    @PostMapping("/save")
    ResponseEntity<UserDto> save(@RequestBody RegisterRequest request);

    @GetMapping("/getUserByUsername/{username}")
    ResponseEntity<UserDto> getUserByUsername(@PathVariable String username);

    @PutMapping("/enable-user")
    ResponseEntity<Void> update(@RequestBody UserDto userDto);
}
