package com.bitescout.app.restaurantservice.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthClient {
    private final RestTemplate restTemplate;

    public AuthClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String login(String username, String password) {
        String url = "http://localhost:8222/v1/auth/login";

        // Create the request body
        LoginRequest loginRequest = new LoginRequest(username, password);

        // Set headers (if needed, like Content-Type)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Create the HttpEntity
        HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest, headers);

        // Send the request
        ResponseEntity<TokenDto> response = restTemplate.exchange(url, HttpMethod.POST, entity, TokenDto.class);

        // Return the token
        return response.getBody().getToken();
    }
}