package com.bitescout.app.userservice.service;
import com.bitescout.app.userservice.client.FileStorageClient;
import com.bitescout.app.userservice.dto.*;
import com.bitescout.app.userservice.entity.*;
import com.bitescout.app.userservice.repository.*;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final FileStorageClient fileStorageClient;

    // USER SERVICES //

    public UserDTO createUser(RegisterRequestDTO request) {
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(request.getRole())
                .build();

        user = userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);

    }

    public UserDTO getUser(String userId) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        return modelMapper.map(user, UserDTO.class);
    }

    private UserDetails updateUserDetails(UserDetails toUpdate, UserDetails request, MultipartFile file) {
        toUpdate = toUpdate == null ? new UserDetails() : toUpdate;

        if (file != null) {
            String profilePicture = fileStorageClient.uploadImageToFIleSystem(file).getBody();
            if (profilePicture != null) {
                fileStorageClient.deleteImageFromFileSystem(toUpdate.getProfilePicture());
                toUpdate.setProfilePicture(profilePicture);
            }
        }

        modelMapper.map(request, toUpdate);
        return toUpdate;
    }
    public UserDTO updateUser(UserUpdateRequestDTO request, MultipartFile profilePicture) {
        User user = userRepository.findById(UUID.fromString(request.getId())).orElseThrow(() -> new RuntimeException("User not found"));

        request.setUserDetails(updateUserDetails(user.getUserDetails(), request.getUserDetails(), profilePicture));
        modelMapper.map(request, user);
        user = userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    public void deleteUser(String userId) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> modelMapper.map(user, UserDTO.class)).collect(Collectors.toList());
    }


    // FAVORITE SERVICES //

    public FavoriteResponseDTO addFavorite(String userId, String restaurantId) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        Favorite favorite = Favorite.builder()
                .user(user)
                .restaurantId(UUID.fromString(restaurantId))
                .build();

        favorite = favoriteRepository.save(favorite);
        return mapToFavoriteResponseDTO(favorite);
    }

    public void deleteFavorite(String userId, String restaurantId) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Favorite> favorite = favoriteRepository.findByUserAndRestaurantId(user, UUID.fromString(restaurantId));
        favorite.ifPresent(favoriteRepository::delete);
    }

    public List<FavoriteResponseDTO> getFavorites(String userId) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        List<Favorite> favorites = favoriteRepository.findByUser(user);
        return favorites.stream().map(this::mapToFavoriteResponseDTO).collect(Collectors.toList());
    }

    public Long countFavorites(String restaurantId) {
        return favoriteRepository.countByRestaurantId(UUID.fromString(restaurantId));
    }

    private FavoriteResponseDTO mapToFavoriteResponseDTO(Favorite favorite) {
        return FavoriteResponseDTO.builder()
                .id(favorite.getId().toString())
                .userId(favorite.getUser().getId().toString())
                .restaurantId(favorite.getRestaurantId().toString())
                .favoritedAt(favorite.getFavoritedAt().toString())
                .build();
    }

}
