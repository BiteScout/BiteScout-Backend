package com.bitescout.app.restaurantservice.service;

import com.bitescout.app.restaurantservice.client.UserServiceClient;
import com.bitescout.app.restaurantservice.dto.RestaurantResponseDTO;
import com.bitescout.app.restaurantservice.dto.UserDTO;
import com.bitescout.app.restaurantservice.exc.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final UserServiceClient userServiceClient;
    private final RestaurantService restaurantService;

    public boolean isOwner(String ownerId, String principal) {
        UserDTO user = userServiceClient.getUser(ownerId).getBody();
        if (user != null) {
            System.out.println("Owner ID: " + ownerId);
            System.out.println("Principal: " + principal);
            System.out.println("Owner Username: " + user.getUsername());
            return user.getUsername().equals(principal);
        }
        return false;
    }


    public String getRestaurantOwnerUsername(String ownerId) {
        try {
            UserDTO user = userServiceClient.getUser(ownerId).getBody();
            if (user == null) {
                return null;
            }
            return user.getUsername();
        } catch (ResourceNotFoundException ex) {
            // Handle case when restaurant is not found, return null or appropriate response
            return null;
        }
    }



    public String getRestaurantOwnerId(String restaurantId) {
        RestaurantResponseDTO restaurant = restaurantService.getRestaurant(restaurantId);
        return restaurant.getOwnerId().toString();
    }
}