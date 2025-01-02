package com.bitescout.app.userservice.service;

import com.bitescout.app.userservice.dto.*;
import com.bitescout.app.userservice.entity.Favorite;
import com.bitescout.app.userservice.entity.User;
import com.bitescout.app.userservice.entity.UserDetails;
import com.bitescout.app.userservice.repository.FavoriteRepository;
import com.bitescout.app.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
    private final BCryptPasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final RestTemplate restTemplate;



    @Value("${spring.email.storage.service.url}")
    private String emailStorageServiceUrl ;

    @Value("${spring.file.storage.service.url}")
    private String fileStorageServiceUrl ;
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

    private UserDetails updateUserDetails(UserDetails toUpdate, UserUpdateRequestDTO request) {
        toUpdate = toUpdate == null ? new UserDetails() : toUpdate;

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
    public String updateUserPicture(String userId, MultipartFile profilePicture) throws IOException {
        // Retrieve the user by userId
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Save the profile picture to a storage location (e.g., file system, cloud storage)
        String userid = user.getId().toString();
        String profilePictureUrl = saveProfilePicture(userid,profilePicture);

        // Update the user's profile picture URL
        user.setProfilePicture(profilePictureUrl);
        userRepository.save(user);

        // Convert the updated user entity to a UserDTO and return it
        return profilePictureUrl;
    }

    private String saveProfilePicture(String userid,MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Profile picture must not be null or empty");
        }

        ByteArrayResource fileResource;
        try {
            fileResource = new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename(); // Return the original filename
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

        // Send POST request
        ResponseEntity<String> response = restTemplate.postForEntity(
                fileStorageServiceUrl + userid +"/upload",
                requestEntity,
                String.class
        );

        return response.getBody();
    }


    public UserDTO updateUser(UserUpdateRequestDTO request) {
        User user = userRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update UserDetails
        user.setUserDetails(updateUserDetails(user.getUserDetails(), request));

        // Map other fields from the request to the User entity
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Save updated user and map to DTO
        user = userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    public UserDTO getUsername(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return modelMapper.map(user, UserDTO.class);
    }

    public String getUserId(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId().toString();
    }

    public void deleteUser(String userId) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    public void deleteUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> modelMapper.map(user, UserDTO.class)).collect(Collectors.toList());
    }


    public Boolean isEnabled(String userId) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        return user.isEnabled();
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

    @Transactional
    public void deleteAllFavoritesByUserId(String userId) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        favoriteRepository.deleteAllByUser(user);
    }

    private FavoriteResponseDTO mapToFavoriteResponseDTO(Favorite favorite) {
        return FavoriteResponseDTO.builder()
                .id(favorite.getId().toString())
                .userId(favorite.getUser().getId().toString())
                .restaurantId(favorite.getRestaurantId().toString())
                .favoritedAt(favorite.getFavoritedAt().toString())
                .build();
    }




    private void saveToEmailList(String email) {
        HttpEntity<String> requestEntity = new HttpEntity<>(email);
        restTemplate.postForEntity(emailStorageServiceUrl + "/emailList", requestEntity, Void.class);
    }


}