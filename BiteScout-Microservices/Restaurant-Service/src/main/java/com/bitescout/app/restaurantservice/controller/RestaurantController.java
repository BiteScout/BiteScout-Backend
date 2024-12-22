package com.bitescout.app.restaurantservice.controller;

import com.bitescout.app.restaurantservice.client.UserServiceClient;
import com.bitescout.app.restaurantservice.dto.*;
import com.bitescout.app.restaurantservice.service.RestaurantService;
import com.bitescout.app.restaurantservice.service.SecurityService;
import com.bitescout.app.restaurantservice.service.SpecialOfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurants/")
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final SpecialOfferService specialOfferService;
    private final SecurityService securityService;   // RESTAURANT ENDPOINTS //

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwner(#restaurantRequest.ownerId, principal)")
    public ResponseEntity<RestaurantResponseDTO> createRestaurant(@Valid @RequestBody RestaurantRequestDTO restaurantRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantService.createRestaurant(restaurantRequest));
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurants() {
        return ResponseEntity.ok(restaurantService.getRestaurants());
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponseDTO> getRestaurant(@PathVariable String restaurantId) {
        return ResponseEntity.ok(restaurantService.getRestaurant(restaurantId));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsByOwnerId(@PathVariable String ownerId) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByOwnerId(ownerId));
    }

    @PutMapping("/{restaurantId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwner(#restaurantRequest.ownerId, principal)")
    public ResponseEntity<RestaurantResponseDTO> updateRestaurant(@PathVariable String restaurantId, @Valid @RequestBody RestaurantRequestDTO restaurantRequest) {
        return ResponseEntity.ok(restaurantService.updateRestaurant(restaurantId, restaurantRequest));
    }

    @GetMapping("/near-me")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsNearMe(@RequestParam double latitude, @RequestParam double longitude, @RequestParam double radius) {
        return ResponseEntity.ok(restaurantService.getRestaurantsNearMe(latitude, longitude, radius));
    }

    @GetMapping("/search")
    public ResponseEntity<List<RestaurantResponseDTO>> searchRestaurants(@RequestParam String restaurantName) {
        return ResponseEntity.ok(restaurantService.searchRestaurants(restaurantName));
    }

    @GetMapping("/cuisine")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsByCuisine(@RequestParam String cuisineType) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByCuisine(cuisineType));
    }

    @GetMapping("/price")
    public ResponseEntity<List<RestaurantResponseDTO>> getRestaurantsByPriceRange(@RequestParam String priceRange) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByPriceRange(priceRange));
    }

    @DeleteMapping("/{restaurantId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.getRestaurantOwnerUsername(@securityService.getRestaurantOwnerId(#restaurantId)) == principal")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable String restaurantId) {
        restaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{restaurantId}/update-menu")
    @PreAuthorize("hasRole('ADMIN') or @securityService.getRestaurantOwnerUsername(@securityService.getRestaurantOwnerId(#restaurantId)) == principal")
    public ResponseEntity<RestaurantResponseDTO> updateMenu(@PathVariable String restaurantId, @RequestParam String menu) {
        return ResponseEntity.ok(restaurantService.updateMenu(restaurantId, menu));
    }

    // SPECIAL OFFER ENDPOINTS //
    @PostMapping("/{restaurantId}/offers")
    @PreAuthorize("hasRole('ADMIN') or @securityService.getRestaurantOwnerUsername(@securityService.getRestaurantOwnerId(#restaurantId)) == principal")
    public ResponseEntity<SpecialOfferResponseDTO> createSpecialOffer(@PathVariable String restaurantId, @Valid @RequestBody SpecialOfferRequestDTO specialOfferRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(specialOfferService.createSpecialOffer(restaurantId, specialOfferRequest));
    }

    @GetMapping("/{restaurantId}/offers")
    public ResponseEntity<List<SpecialOfferResponseDTO>> getSpecialOffers(@PathVariable String restaurantId) {
        return ResponseEntity.ok(specialOfferService.getSpecialOffers(restaurantId));
    }

    @PutMapping("/{restaurantId}/offers/{offerId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.getRestaurantOwnerUsername(@securityService.getRestaurantOwnerId(#restaurantId)) == principal")
    public ResponseEntity<SpecialOfferResponseDTO> updateSpecialOffer(@PathVariable String restaurantId, @PathVariable String offerId, @Valid @RequestBody SpecialOfferRequestDTO specialOfferRequest) {
        return ResponseEntity.ok(specialOfferService.updateSpecialOffer(restaurantId, offerId, specialOfferRequest));
    }

    @DeleteMapping("/{restaurantId}/offers/{offerId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.getRestaurantOwnerUsername(@securityService.getRestaurantOwnerId(#restaurantId)) == principal")
    public ResponseEntity<Void> deleteSpecialOffer(@PathVariable String restaurantId, @PathVariable String offerId) {
        specialOfferService.deleteSpecialOffer(restaurantId, offerId);
        return ResponseEntity.noContent().build();
    }

}