package com.bitescout.app.restaurantservice.service;

import com.bitescout.app.restaurantservice.dto.*;
import com.bitescout.app.restaurantservice.repository.*;
import com.bitescout.app.restaurantservice.entity.*;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ModelMapper modelMapper;

    public RestaurantResponseDTO createRestaurant(RestaurantRequestDTO restaurantRequest) {
        Restaurant restaurant = modelMapper.map(restaurantRequest, Restaurant.class);
        return modelMapper.map(restaurantRepository.save(restaurant), RestaurantResponseDTO.class);
    }

    public List<RestaurantResponseDTO> getRestaurants() {
        return restaurantRepository.findAll().stream()
                .map(restaurant -> modelMapper.map(restaurant, RestaurantResponseDTO.class))
                .collect(Collectors.toList());
    }

    public RestaurantResponseDTO getRestaurant(String restaurantId) {
        return modelMapper.map(restaurantRepository.findById(UUID.fromString(restaurantId))
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found")), RestaurantResponseDTO.class);
    }

    public List<RestaurantResponseDTO> getRestaurantsByOwnerId(String ownerId) {
        return restaurantRepository.findByOwnerId(UUID.fromString(ownerId)).stream()
                .map(restaurant -> modelMapper.map(restaurant, RestaurantResponseDTO.class))
                .collect(Collectors.toList());
    }

    public RestaurantResponseDTO updateRestaurant(String restaurantId, RestaurantRequestDTO restaurantRequest) {
        Restaurant restaurant = restaurantRepository.findById(UUID.fromString(restaurantId))
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        modelMapper.map (restaurantRequest, restaurant);
        return modelMapper.map(restaurantRepository.save(restaurant), RestaurantResponseDTO.class);
    }

    public List<RestaurantResponseDTO> getRestaurantsNearMe(double latitude, double longitude, double radius) {
        return restaurantRepository.findByLocationNear(new Point(latitude, longitude), new Distance(radius, Metrics.KILOMETERS)).stream()
                .map(restaurant -> modelMapper.map(restaurant, RestaurantResponseDTO.class))
                .collect(Collectors.toList());
    }

    public List<RestaurantResponseDTO> searchRestaurants(String restaurantName) {
        return restaurantRepository.findByNameContainingIgnoreCase(restaurantName).stream()
                .map(restaurant -> modelMapper.map(restaurant, RestaurantResponseDTO.class))
                .collect(Collectors.toList());
    }

    public List<RestaurantResponseDTO> getRestaurantsByCuisine(String cuisineType) {
        return restaurantRepository.findByCuisineTypeContainingIgnoreCase(cuisineType).stream()
                .map(restaurant -> modelMapper.map(restaurant, RestaurantResponseDTO.class))
                .collect(Collectors.toList());
    }

    public List<RestaurantResponseDTO> getRestaurantsByPriceRange(String priceRange) {
        return restaurantRepository.findByPriceRangeContainingIgnoreCase(priceRange).stream()
                .map(restaurant -> modelMapper.map(restaurant, RestaurantResponseDTO.class))
                .collect(Collectors.toList());
    }

    public void deleteRestaurant(String restaurantId) {
        restaurantRepository.deleteById(UUID.fromString(restaurantId));
    }

    public RestaurantResponseDTO updateMenu(String restaurantId, String menu) {
        Restaurant restaurant = restaurantRepository.findById(UUID.fromString(restaurantId))
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        restaurant.setMenu(menu);
        return modelMapper.map(restaurantRepository.save(restaurant), RestaurantResponseDTO.class);
    }
}
