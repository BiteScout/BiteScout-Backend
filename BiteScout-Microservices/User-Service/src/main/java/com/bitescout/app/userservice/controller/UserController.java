package com.bitescout.app.userservice.controller;

import com.bitescout.app.userservice.dto.*;
import com.bitescout.app.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/users")
public class UserController {
    private final UserService userService;

    // USER ENDPOINTS //

    @PostMapping("/save")
    public ResponseEntity<UserAuthDTO> createUser(@Valid @RequestBody RegisterRequestDTO RegisterRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(RegisterRequest));
    }
    @GetMapping("/getUsername/{userId}")
    public ResponseEntity<UserDTO> getUsername(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUsername(userId));
    }
    @GetMapping("/getUserByUsername/{username}")
    public ResponseEntity<UserAuthDTO> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @GetMapping("/getUserId/{username}")
    public ResponseEntity<String> getUserId(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserId(username));
    }

    @PutMapping("/enable-user/{userId}")
    public ResponseEntity<Boolean> enableUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.enableUser(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or @userService.getUser(#request.id).username == principal")
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserUpdateRequestDTO request) {
        return ResponseEntity.ok(userService.updateUser(request));

    }

    @PutMapping("/update-picture/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userService.getUser(#userId).username == principal")
    public ResponseEntity<String> updateUserPicture(@PathVariable String userId, @RequestPart MultipartFile image) throws IOException {
        return ResponseEntity.ok(userService.updateUserPicture(userId, image));
    }

    @DeleteMapping("/delete-picture/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userService.getUser(#userId).username == principal")
    public ResponseEntity<Void> deleteUserPicture(@PathVariable String userId) {
        userService.deleteUserPicture(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getPicture/{userId}")
    public ResponseEntity<String> getUserPicture(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getProfilePicture(userId));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or (@userService.getUser(#userId).username == principal)")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN') or (@userService.getUserByUsername(#username).username == principal)")
    public ResponseEntity<Void> deleteUserByUsername(@PathVariable String username) {
        userService.deleteUserByUsername(username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/isEnabled/{userId}")
    public ResponseEntity<Boolean> isEnabled(@PathVariable String userId) {
        return ResponseEntity.ok(userService.isEnabled(userId));
    }

    // FAVORITES ENDPOINTS //

    @PostMapping("/{userId}/favorites/{restaurantId}")
    @PreAuthorize("hasRole('ADMIN') or (@userService.getUser(#userId).username == principal and @userService.getUser(#userId).enabled == true)")
    public ResponseEntity<FavoriteResponseDTO> addFavorite(@PathVariable String userId, @PathVariable String restaurantId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addFavorite(userId, restaurantId));
    }

    @GetMapping("/{userId}/favorites")
    public ResponseEntity<List<FavoriteResponseDTO>> getFavorites(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getFavorites(userId));
    }

    @DeleteMapping("/{userId}/favorites/{restaurantId}")
    @PreAuthorize("hasRole('ADMIN') or (@userService.getUser(#userId).username == principal and @userService.getUser(#userId).enabled == true)")
    public ResponseEntity<Void> deleteFavorite(@PathVariable String userId, @PathVariable String restaurantId) {
        userService.deleteFavorite(userId, restaurantId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favoriteCount/{restaurantId}")
    public ResponseEntity<Long> countFavorites(@PathVariable String restaurantId) {
        return ResponseEntity.ok(userService.countFavorites(restaurantId));
    }

    @DeleteMapping("/deleteAllFavorites/{userId}")
    public ResponseEntity<Void> deleteAllFavoritesByUserId(@PathVariable String userId) {
        userService.deleteAllFavoritesByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getUsersByFavRestaurant/{restaurantId}")
    public ResponseEntity<List<UserDTO>> getUsersByFavoritedRestaurant(@PathVariable String restaurantId) {
        return ResponseEntity.ok(userService.getUsersByFavoritedRestaurant(restaurantId));
    }

}
