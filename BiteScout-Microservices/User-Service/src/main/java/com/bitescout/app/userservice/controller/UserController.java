package com.bitescout.app.userservice.controller;
import com.bitescout.app.userservice.dto.*;
import com.bitescout.app.userservice.service.UserService;

import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequestDTO));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable String userId, @RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.ok(userService.updateUser(userId, userRequestDTO));
    }

    @PatchMapping("/{userId}/profile-picture")
    public ResponseEntity<UserResponseDTO> updateProfilePicture(@PathVariable String userId, @RequestBody ProfilePictureRequestDTO profilePictureRequestDTO) {
        return ResponseEntity.ok(userService.updateProfilePicture(userId, profilePictureRequestDTO));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/{userId}/favorites")
    public ResponseEntity<FavoriteResponseDTO> addFavorite(@PathVariable String userId, @RequestBody FavoriteRequestDTO favoriteRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addFavorite(userId, favoriteRequestDTO));
    }

    @GetMapping("/{userId}/favorites")
    public ResponseEntity<List<FavoriteResponseDTO>> getFavorites(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getFavorites(userId));
    }

    @DeleteMapping("/{userId}/favorites/{restaurantId}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable String userId, @PathVariable String restaurantId) {
        userService.deleteFavorite(userId, restaurantId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favorites/countFavorites/{restaurantId}")
    public ResponseEntity<Long> countFavorites(@PathVariable String restaurantId) {
        return ResponseEntity.ok(userService.countFavorites(restaurantId));
    }
}
