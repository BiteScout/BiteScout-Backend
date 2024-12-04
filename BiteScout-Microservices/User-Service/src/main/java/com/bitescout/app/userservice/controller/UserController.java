package com.bitescout.app.userservice.controller;

import com.bitescout.app.userservice.dto.FavoriteResponseDTO;
import com.bitescout.app.userservice.dto.RegisterRequestDTO;
import com.bitescout.app.userservice.dto.UserDTO;
import com.bitescout.app.userservice.dto.UserUpdateRequestDTO;
import com.bitescout.app.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/")
public class UserController {
    private final UserService userService;

    // USER ENDPOINTS //

    @PostMapping("/save")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody RegisterRequestDTO RegisterRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(RegisterRequest));
    }

    @PutMapping("/enable-user")
    public ResponseEntity<UserDTO> enableUser(@RequestBody UserDTO userDto) {
        return ResponseEntity.ok(userService.enableUser(userDto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or @userService.getUser(#request.id).username == principal")
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestPart UserUpdateRequestDTO request,
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
