package com.bitescout.app.restaurantservice.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor  // Generates a constructor with all fields
@NoArgsConstructor   // Generates a no-args constructor
public class LoginRequest {
    private String username;
    private String password;
}