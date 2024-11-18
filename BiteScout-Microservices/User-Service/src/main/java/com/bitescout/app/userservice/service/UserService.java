package com.bitescout.app.userservice.service;
import com.bitescout.app.userservice.dto.*;
import com.bitescout.app.userservice.entity.*;
import com.bitescout.app.userservice.repository.*;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final ModelMapper modelMapper;

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        User user = modelMapper.map(userRequestDTO, User.class);
        user = userRepository.save(user);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public UserResponseDTO getUser(String userId) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public UserResponseDTO updateUser(String userId, UserRequestDTO userRequestDTO) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update only non-null fields
        if (userRequestDTO.getFirstName() != null) user.setFirstName(userRequestDTO.getFirstName());
        if (userRequestDTO.getLastName() != null) user.setLastName(userRequestDTO.getLastName());
        if (userRequestDTO.getEmail() != null) user.setEmail(userRequestDTO.getEmail());
        if (userRequestDTO.getPassword() != null) user.setPassword(userRequestDTO.getPassword());
        if (userRequestDTO.getPhoneNumber() != null) user.setPhoneNumber(userRequestDTO.getPhoneNumber());
        if (userRequestDTO.getAddress() != null) user.setAddress(userRequestDTO.getAddress());
        if (userRequestDTO.getProfilePicture() != null) user.setProfilePicture(userRequestDTO.getProfilePicture());
        if (userRequestDTO.getRole() != null) user.setRole(userRequestDTO.getRole());
        if (userRequestDTO.getIsVerified() != null) user.setIsVerified(userRequestDTO.getIsVerified());
        if (userRequestDTO.getVerificationToken() != null) user.setVerificationToken(userRequestDTO.getVerificationToken());
        if (userRequestDTO.getOauthProvider() != null) user.setOauthProvider(userRequestDTO.getOauthProvider());
        if (userRequestDTO.getOauthId() != null) user.setOauthId(userRequestDTO.getOauthId());

        return modelMapper.map(userRepository.save(user), UserResponseDTO.class);
    }


    public UserResponseDTO updateProfilePicture(String userId, ProfilePictureRequestDTO profilePictureRequestDTO) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        user.setProfilePicture(profilePictureRequestDTO.getProfilePicture());
        user = userRepository.save(user);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public void deleteUser(String userId) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> modelMapper.map(user, UserResponseDTO.class)).collect(Collectors.toList());
    }

    public FavoriteResponseDTO addFavorite(String userId, FavoriteRequestDTO favoriteRequestDTO) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        // create favorite with builder
        Favorite favorite = Favorite.builder()
                .user(user)
                .restaurantId(UUID.fromString(favoriteRequestDTO.getRestaurantId()))
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
                .build();
    }

}
