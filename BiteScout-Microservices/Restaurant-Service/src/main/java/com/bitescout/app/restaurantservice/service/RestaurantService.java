package com.bitescout.app.restaurantservice.service;

import com.bitescout.app.restaurantservice.dto.*;
import com.bitescout.app.restaurantservice.exc.ResourceNotFoundException;
import com.bitescout.app.restaurantservice.repository.*;
import com.bitescout.app.restaurantservice.entity.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    @Value("${spring.file.storage.service.url}")
    private String fileStorageServiceUrl;
    private final RestaurantRepository restaurantRepository;
    private final ImagesRepository imagesRepository;
    private final ModelMapper modelMapper;
    private final RestTemplate restTemplate;

    public RestaurantResponseDTO createRestaurant(RestaurantRequestDTO restaurantRequest) {
        Restaurant restaurant = modelMapper.map(restaurantRequest, Restaurant.class);
        return modelMapper.map(restaurantRepository.save(restaurant), RestaurantResponseDTO.class);
    }

    public List<RestaurantResponseDTO> getRestaurants() {
        return restaurantRepository.findAll().stream()
                .map(restaurant -> modelMapper.map(restaurant, RestaurantResponseDTO.class))
                .collect(Collectors.toList());
    }
    public String saveImage(String restaurantId,MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Profile picture must not be null or empty");
        }

        ByteArrayResource fileResource;
        try {
            fileResource = new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    if(image.getOriginalFilename() == null) {
                        return UUID.randomUUID().toString();
                    }
                    return image.getOriginalFilename();
                }
            };
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file bytes", e);
        }

        // Set headers for the multipart request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Prepare the body as part of a multipart request
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", fileResource);

        // Create HttpEntity
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Send POST request to the worker service
        ResponseEntity<String> response = restTemplate.postForEntity(
                fileStorageServiceUrl + restaurantId + "/upload",
                requestEntity,
                String.class
        );

        String imageUrl = response.getBody();

        Restaurant restaurant = restaurantRepository.findById(UUID.fromString(restaurantId))
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        if (restaurant.getImages() == null) {
            restaurant.setImages(new ArrayList<>());
        }


        // Create a new Images entity
        Images newImage = Images.builder()
                .imageUrl(imageUrl)
                .restaurant(restaurant)
                .build();

        // Save the new image entity
        imagesRepository.save(newImage);

        return response.getBody();
    }

    @Transactional
    public void deleteImage(String restaurantId) {
        // Send DELETE request to the worker service
        restTemplate.delete(fileStorageServiceUrl + restaurantId + "/delete");
        Restaurant restaurant = restaurantRepository.findById(UUID.fromString(restaurantId))
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        imagesRepository.deleteAllByRestaurant(restaurant);
    }

    public List<String> getImage(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(UUID.fromString(restaurantId))
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        List<Images> images = imagesRepository.findAllByRestaurant(restaurant);
        List<String> imageUrls = new ArrayList<>();
        for (Images image : images) {
            imageUrls.add(image.getImageUrl());
        }
        return imageUrls;
    }



    public RestaurantResponseDTO getRestaurant(String restaurantId) {
        System.out.println("Fetching restaurant with ID: " + restaurantId);
        return restaurantRepository.findById(UUID.fromString(restaurantId))
                .map(restaurant -> modelMapper.map(restaurant, RestaurantResponseDTO.class))
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));
    }

    public List<String> getAllCuisines() {
        //first get all restaurants
        List<Restaurant> restaurants = restaurantRepository.findAll();
        //then get all cuisines
        List<String> cuisines = new ArrayList<>();
        for (Restaurant restaurant : restaurants) {
            if (!cuisines.contains(restaurant.getCuisineType())) {
                cuisines.add(restaurant.getCuisineType());
            }
        }
        return cuisines;
    }

    public List<RestaurantResponseDTO> getRestaurantsByOwnerId(String ownerId) {
        return restaurantRepository.findByOwnerId(UUID.fromString(ownerId)).stream()
                .map(restaurant -> modelMapper.map(restaurant, RestaurantResponseDTO.class))
                .collect(Collectors.toList());
    }

    public RestaurantResponseDTO updateRestaurant(String restaurantId, RestaurantRequestDTO restaurantRequest) {
        Restaurant restaurant = restaurantRepository.findById(UUID.fromString(restaurantId))
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        restaurant.setOwnerId(restaurantRequest.getOwnerId());
        restaurant.setName(restaurantRequest.getName());
        restaurant.setDescription(restaurantRequest.getDescription());
        restaurant.setMenu(restaurantRequest.getMenu());
        restaurant.setCuisineType(restaurantRequest.getCuisineType());
        restaurant.setLocation(restaurantRequest.getLocation());
        restaurant.setPriceRange(restaurantRequest.getPriceRange());

        return modelMapper.map(restaurantRepository.save(restaurant), RestaurantResponseDTO.class);
    }

    public List<RestaurantResponseDTO> getRestaurantsNearMe(double latitude, double longitude, double radiusInKm) {
        double radiusInMeters = radiusInKm * 1000; //
        return restaurantRepository.findByLocationNear(latitude, longitude, radiusInMeters).stream()
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
        return restaurantRepository.findByPriceRangeIgnoreCase(priceRange).stream()
                .map(restaurant -> modelMapper.map(restaurant, RestaurantResponseDTO.class))
                .collect(Collectors.toList());
    }

    public String getRestaurantIdByName(String restaurantName) {
        return restaurantRepository.findByName(restaurantName).getId().toString();
    }

    public void deleteRestaurant(String restaurantId) {
        restaurantRepository.deleteById(UUID.fromString(restaurantId));
    }

    public void deleteAllRestaurants() {
        restaurantRepository.deleteAll();
    }

    public RestaurantResponseDTO updateMenu(String restaurantId, String menu) {
        Restaurant restaurant = restaurantRepository.findById(UUID.fromString(restaurantId))
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        restaurant.setMenu(menu);
        return modelMapper.map(restaurantRepository.save(restaurant), RestaurantResponseDTO.class);
    }
}
