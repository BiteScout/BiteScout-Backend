package com.bitescout.app.userservice.service;

import com.bitescout.app.userservice.dto.*;
import com.bitescout.app.userservice.entity.Favorite;
import com.bitescout.app.userservice.entity.User;
import com.bitescout.app.userservice.entity.UserDetails;
import com.bitescout.app.userservice.repository.FavoriteRepository;
import com.bitescout.app.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final RestTemplate restTemplate;

    @Value("${spring.file.storage.service.url}")
    private String fileStorageServiceUrl;

    // USER SERVICES //

    public UserAuthDTO createUser(RegisterRequestDTO request) {
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(request.getRole())
                .enabled(false)
                .build();

        user = userRepository.save(user);
        // every new user is added to the email list bucket cloud storage for weekly newsletter
        saveToEmailList(user.getEmail());
        return modelMapper.map(user, UserAuthDTO.class);

    }

    public Boolean enableUser(String userId) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(true);
        user = userRepository.save(user);
        return user.isEnabled();
    }

    public UserAuthDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return modelMapper.map(user, UserAuthDTO.class);
    }

    public UserDTO getUser(String userId) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        return modelMapper.map(user, UserDTO.class);
    }

    private UserDetails updateUserDetails(UserDetails toUpdate, UserUpdateRequestDTO request, MultipartFile file) {
        toUpdate = toUpdate == null ? new UserDetails() : toUpdate;

        if (file != null) {
            String profilePicture = uploadImageToFileSystem(file);
            if (profilePicture != null) {
                deleteImageFromFileSystem(toUpdate.getProfilePicture());
                toUpdate.setProfilePicture(profilePicture);
            }
        }

        // Set fields from the updated request
        toUpdate.setFirstName(request.getFirstName());
        toUpdate.setLastName(request.getLastName());
        toUpdate.setPhoneNumber(request.getPhoneNumber());
        toUpdate.setCountry(request.getCountry());
        toUpdate.setCity(request.getCity());
        toUpdate.setPostalCode(request.getPostalCode());
        toUpdate.setAddress(request.getAddress());

        return toUpdate;
    }


    public UserDTO updateUser(UserUpdateRequestDTO request, MultipartFile profilePicture) {
        User user = userRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update UserDetails
        user.setUserDetails(updateUserDetails(user.getUserDetails(), request, profilePicture));

        // Map other fields from the request to the User entity
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getPassword() != null) {
            user.setPassword(request.getPassword());
        }

        // Save updated user and map to DTO
        user = userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    public UserDTO getUsername(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
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

    private String uploadImageToFileSystem(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            Resource fileAsResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            HttpEntity<Resource> requestEntity = new HttpEntity<>(fileAsResource, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(fileStorageServiceUrl + "/upload", requestEntity, String.class);

            return response.getBody();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file bytes", e);
        }
    }

    private void deleteImageFromFileSystem(String filename) {
        restTemplate.delete(fileStorageServiceUrl + "/delete/" + filename);
    }
    private void saveToEmailList(String email) {
        HttpEntity<String> requestEntity = new HttpEntity<>(email);
        restTemplate.postForEntity(fileStorageServiceUrl + "/emailList", requestEntity, Void.class);
    }


}