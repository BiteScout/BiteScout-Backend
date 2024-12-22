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

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/")
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
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserUpdateRequestDTO request,
                                              @RequestPart(required = false) MultipartFile profilePicture) {
        return ResponseEntity.ok(userService.updateUser(request, profilePicture));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userService.getUser(#userId).username == principal")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    // FAVORITES ENDPOINTS //

    @PostMapping("/{userId}/favorites/{restaurantId}")
    @PreAuthorize("hasRole('ADMIN') or @userService.getUser(#userId).username == principal")
    public ResponseEntity<FavoriteResponseDTO> addFavorite(@PathVariable String userId, @PathVariable String restaurantId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addFavorite(userId, restaurantId));
    }

    @GetMapping("/{userId}/favorites")
    public ResponseEntity<List<FavoriteResponseDTO>> getFavorites(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getFavorites(userId));
    }

    @DeleteMapping("/{userId}/favorites/{restaurantId}")
    @PreAuthorize("hasRole('ADMIN') or @userService.getUser(#userId).username == principal")
    public ResponseEntity<Void> deleteFavorite(@PathVariable String userId, @PathVariable String restaurantId) {
        userService.deleteFavorite(userId, restaurantId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favoriteCount/{restaurantId}")
    public ResponseEntity<Long> countFavorites(@PathVariable String restaurantId) {
        return ResponseEntity.ok(userService.countFavorites(restaurantId));
    }


}
