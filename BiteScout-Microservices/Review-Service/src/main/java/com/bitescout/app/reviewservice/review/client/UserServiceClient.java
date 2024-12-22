package com.bitescout.app.reviewservice.review.client;

import com.bitescout.app.reviewservice.review.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "api/v1/users")
public interface UserServiceClient {
    @GetMapping("/{userId}")
    ResponseEntity<UserDTO> getUser(@PathVariable String userId);


}
