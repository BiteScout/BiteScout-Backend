package com.bitescout.app.userservice;

import com.bitescout.app.userservice.client.AuthClient;
import com.bitescout.app.userservice.dto.*;
import com.bitescout.app.userservice.entity.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthClient authClient;

    // TC-01: Create a user with valid data
    @Test
    public void createUser_ValidData_ShouldReturn201() throws Exception {
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("testuser")
                .password("Test1234")
                .email("testuser@example.com")
                .role(Role.CUSTOMER)
                .build();

        mockMvc.perform(post("/v1/users/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"));

        // Cleanup
        String token = authClient.login("admin", "$2a$10$2529eBq3R6Y41t03Mku2I.2Nh3W0p25lt.s.85mG0kiAvxI4bsAHa");
        mockMvc.perform(delete("/v1/users/username/{username}", "testuser")
                .header("Authorization", "Bearer " + token));
    }

    // TC-02: Attempt to create a user with missing username
    @Test
    public void createUser_MissingUsername_ShouldReturn400() throws Exception {
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .password("Test1234")
                .email("testuser@example.com")
                .role(Role.CUSTOMER)
                .build();

        mockMvc.perform(post("/v1/users/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // TC-03: Retrieve a user's username by their userId
    @Test
    public void getUsernameByUserId_ValidId_ShouldReturn200() throws Exception {
        // Create a user to retrieve their username
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("testuser")
                .password("Test1234")
                .email("testuser@example.com")
                .role(Role.CUSTOMER)
                .build();

        mockMvc.perform(post("/v1/users/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // get the userId
        String userId = mockMvc.perform(get("/v1/users/getUserId/{username}", "testuser"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // get the username by userId
        mockMvc.perform(get("/v1/users/getUsername/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));

        // Cleanup
        String token = authClient.login("admin", "$2a$10$2529eBq3R6Y41t03Mku2I.2Nh3W0p25lt.s.85mG0kiAvxI4bsAHa");
        mockMvc.perform(delete("/v1/users/username/{username}", "testuser")
                .header("Authorization", "Bearer " + token));

    }

    // TC-04: Update a user with valid inputs including a profile picture
    @Test
    public void updateUser_ValidInputs_ShouldReturn200() throws Exception {
        String token = authClient.login("admin", "$2a$10$2529eBq3R6Y41t03Mku2I.2Nh3W0p25lt.s.85mG0kiAvxI4bsAHa");
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("testuser")
                .password("Test1234")
                .email("testuser@example.com")
                .role(Role.CUSTOMER)
                .build();

        mockMvc.perform(post("/v1/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //fetch the userId
        String userId = mockMvc.perform(get("/v1/users/getUserId/{username}", "testuser"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();


        // Update the user
        UserUpdateRequestDTO updateRequest = UserUpdateRequestDTO.builder()
                .id(userId)
                .username("updatedUsername")
                .profilePicture("profile.jpg")
                .build();

        mockMvc.perform(put("/v1/users/update")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updatedUsername"));

        // Cleanup
        mockMvc.perform(delete("/v1/users/username/{username}", "updatedUsername")
                .header("Authorization", "Bearer " + token));

    }

    // TC-05: Attempt to update a user with invalid inputs
    @Test
    public void updateUser_InvalidInputs_ShouldReturn401() throws Exception {
        String token = "wrong token";
        String correctToken = authClient.login("admin", "$2a$10$2529eBq3R6Y41t03Mku2I.2Nh3W0p25lt.s.85mG0kiAvxI4bsAHa");
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("testuser")
                .password("Test1234")
                .email("testuser@example.com")
                .role(Role.CUSTOMER)
                .build();

        mockMvc.perform(post("/v1/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //fetch the userId
        String userId = mockMvc.perform(get("/v1/users/getUserId/{username}", "testuser"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();


        // Update the user
        UserUpdateRequestDTO updateRequest = UserUpdateRequestDTO.builder()
                .id(userId)
                .username("updatedUsername")
                .profilePicture("profile.jpg")
                .build();

        mockMvc.perform(put("/v1/users/update")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized());

        // Cleanup
        mockMvc.perform(delete("/v1/users/username/{username}", "testuser")
                .header("Authorization", "Bearer " + correctToken));
    }

    // TC-06: Add a restaurant to a user's favorites
    @Test
    public void addFavorite_ValidData_ShouldReturn201() throws Exception {
        // Create a user to add a favorite
        String correctToken = authClient.login("admin", "$2a$10$2529eBq3R6Y41t03Mku2I.2Nh3W0p25lt.s.85mG0kiAvxI4bsAHa");
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("testuser")
                .password("Test1234")
                .email("testuser@example.com")
                .role(Role.CUSTOMER)
                .build();

        mockMvc.perform(post("/v1/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //fetch the userId
        String userId = mockMvc.perform(get("/v1/users/getUserId/{username}", "testuser"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String restaurantId = UUID.randomUUID().toString();

        mockMvc.perform(post("/v1/users/{userId}/favorites/{restaurantId}", userId, restaurantId)
                        .header("Authorization", "Bearer " + correctToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // Cleanup
        mockMvc.perform(delete("/v1/users//deleteAllFavorites/{userId}", userId)
                .header("Authorization", "Bearer " + correctToken));

        mockMvc.perform(delete("/v1/users/username/{username}", "testuser")
                .header("Authorization", "Bearer " + correctToken));
    }

    // TC-07: Retrieve all favorites for a user
    @Test
    public void getFavorites_ValidUserId_ShouldReturn200() throws Exception {

        // Create a user to add a favorite first
        String correctToken = authClient.login("admin", "$2a$10$2529eBq3R6Y41t03Mku2I.2Nh3W0p25lt.s.85mG0kiAvxI4bsAHa");
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("testuser")
                .password("Test1234")
                .email("testuser@example.com")
                .role(Role.CUSTOMER)
                .build();

        mockMvc.perform(post("/v1/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //fetch the userId
        String userId = mockMvc.perform(get("/v1/users/getUserId/{username}", "testuser"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String restaurant1 = UUID.randomUUID().toString();
        String restaurant2 = UUID.randomUUID().toString();
        String restaurant3 = UUID.randomUUID().toString();

        // Add 3 restaurants to favorites

        mockMvc.perform(post("/v1/users/{userId}/favorites/{restaurantId}", userId, restaurant1)
                        .header("Authorization", "Bearer " + correctToken)
                        .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/v1/users/{userId}/favorites/{restaurantId}", userId, restaurant2)
                        .header("Authorization", "Bearer " + correctToken)
                        .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/v1/users/{userId}/favorites/{restaurantId}", userId, restaurant3)
                        .header("Authorization", "Bearer " + correctToken)
                        .contentType(MediaType.APPLICATION_JSON));

        // Get the favorites
        mockMvc.perform(get("/v1/users/{userId}/favorites", userId)
                        .header("Authorization", "Bearer " + correctToken))
                .andExpect(status().isOk());

        // Cleanup
        mockMvc.perform(delete("/v1/users//deleteAllFavorites/{userId}", userId)
                        .header("Authorization", "Bearer " + correctToken));

        mockMvc.perform(delete("/v1/users/username/{username}", "testuser")
                .header("Authorization", "Bearer " + correctToken));
    }

    // TC-08: Remove a restaurant from a user's favorites
    @Test
    public void deleteFavorite_ValidData_ShouldReturn204() throws Exception {
        // Create a user to add a favorite
        String correctToken = authClient.login("admin", "$2a$10$2529eBq3R6Y41t03Mku2I.2Nh3W0p25lt.s.85mG0kiAvxI4bsAHa");
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("testuser")
                .password("Test1234")
                .email("testuser@example.com")
                .role(Role.CUSTOMER)
                .build();

        mockMvc.perform(post("/v1/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //fetch the userId
        String userId = mockMvc.perform(get("/v1/users/getUserId/{username}", "testuser"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String restaurantId = UUID.randomUUID().toString();

        // Add a restaurant to favorites
        mockMvc.perform(post("/v1/users/{userId}/favorites/{restaurantId}", userId, restaurantId)
                        .header("Authorization", "Bearer " + correctToken)
                        .contentType(MediaType.APPLICATION_JSON));

        // Remove the restaurant from favorites
        mockMvc.perform(delete("/v1/users/{userId}/favorites/{restaurantId}", userId, restaurantId)
                        .header("Authorization", "Bearer " + correctToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Cleanup
        mockMvc.perform(delete("/v1/users//deleteAllFavorites/{userId}", userId)
                .header("Authorization", "Bearer " + correctToken));

        mockMvc.perform(delete("/v1/users/username/{username}", "testuser")
                .header("Authorization", "Bearer " + correctToken));
    }

    // TC-09: Count the number of users who favorited a specific restaurant
    @Test
    public void countFavorites_ValidRestaurantId_ShouldReturn200() throws Exception {
        // Create a user to add a favorite first
        String correctToken = authClient.login("admin", "$2a$10$2529eBq3R6Y41t03Mku2I.2Nh3W0p25lt.s.85mG0kiAvxI4bsAHa");
        RegisterRequestDTO request1 = RegisterRequestDTO.builder()
                .username("user111")
                .password("User1Pass")
                .email("user111@example.com")
                .role(Role.CUSTOMER)
                .build();

        RegisterRequestDTO request2 = RegisterRequestDTO.builder()
                .username("user222")
                .password("User2Pass")
                .email("user222@example.com")
                .role(Role.CUSTOMER)
                .build();

        mockMvc.perform(post("/v1/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)));
        mockMvc.perform(post("/v1/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)));

        //fetch the userIds
        String userId1 = mockMvc.perform(get("/v1/users/getUserId/{username}", "user111"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String userId2 = mockMvc.perform(get("/v1/users/getUserId/{username}", "user222"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String restaurantId = UUID.randomUUID().toString();

        // Add 2 favorites for the restaurant
        mockMvc.perform(post("/v1/users/{userId}/favorites/{restaurantId}", userId1, restaurantId)
                .header("Authorization", "Bearer " + correctToken)
                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(post("/v1/users/{userId}/favorites/{restaurantId}", userId2, restaurantId)
                .header("Authorization", "Bearer " + correctToken)
                .contentType(MediaType.APPLICATION_JSON));

        //test the count
        mockMvc.perform(get("/v1/users/favoriteCount/{restaurantId}", restaurantId)
                .header("Authorization", "Bearer " + correctToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(2));

        // Cleanup
        mockMvc.perform(delete("/v1/users//deleteAllFavorites/{userId}", userId1)
                .header("Authorization", "Bearer " + correctToken));
        mockMvc.perform(delete("/v1/users//deleteAllFavorites/{userId}", userId2)
                .header("Authorization", "Bearer " + correctToken));
        mockMvc.perform(delete("/v1/users/username/{username}", "user111")
                .header("Authorization", "Bearer " + correctToken));
        mockMvc.perform(delete("/v1/users/username/{username}", "user222")
                .header("Authorization", "Bearer " + correctToken));
    }

    // TC-10: Enable a user account
    @Test
    public void enableUser_ValidUserId_ShouldReturn200() throws Exception {
        String token = authClient.login("admin", "$2a$10$2529eBq3R6Y41t03Mku2I.2Nh3W0p25lt.s.85mG0kiAvxI4bsAHa");
        // Create a user to enable
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("testuser")
                .password("Test1234")
                .email("testuser@example.com")
                .role(Role.CUSTOMER)
                .build();

        mockMvc.perform(post("/v1/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //fetch the userId
        String userId = mockMvc.perform(get("/v1/users/getUserId/{username}", "testuser"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Enable the user
        mockMvc.perform(put("/v1/users/enable-user/{userId}", userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Cleanup
        mockMvc.perform(delete("/v1/users/username/{username}", "testuser")
                .header("Authorization", "Bearer " + token));

    }

    // TC-11: Attempt to delete a user account by a non-admin without matching username
    @Test
    public void deleteUser_NonAdminWithoutMatchingUsername_ShouldReturn401() throws Exception {
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("testuser")
                .password("Test1234")
                .email("testuser@example.com")
                .role(Role.CUSTOMER)
                .build();

        mockMvc.perform(post("/v1/users/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        //fetch the userId
        String userId = mockMvc.perform(get("/v1/users/getUserId/{username}", "testuser"))
                .andReturn().getResponse().getContentAsString();

        //test with wrong token
        String wrong_token = "wrong token";
        mockMvc.perform(delete("/v1/users/{userId}", userId)
                        .header("Authorization", "Bearer " + wrong_token))
                .andExpect(status().isUnauthorized());

        // Cleanup
        String token = authClient.login("admin", "$2a$10$2529eBq3R6Y41t03Mku2I.2Nh3W0p25lt.s.85mG0kiAvxI4bsAHa");
        mockMvc.perform(delete("/v1/users/username/{username}", "testuser")
                .header("Authorization", "Bearer " + token));
    }

    // TC-12: Attempt to delete a user account by an admin or with matching username
    @Test
    public void deleteUser_AdminOrMatchingUsername_ShouldReturn200() throws Exception {
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("testuser")
                .password("Test1234")
                .email("testuser@example.com")
                .role(Role.CUSTOMER)
                .build();

        mockMvc.perform(post("/v1/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Cleanup and test with admin token
        String token = authClient.login("admin", "$2a$10$2529eBq3R6Y41t03Mku2I.2Nh3W0p25lt.s.85mG0kiAvxI4bsAHa");
        String userId = mockMvc.perform(get("/v1/users/getUserId/{username}", "testuser"))
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(delete("/v1/users/{userId}", userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    // TC-13: Retrieve all users as an admin
    @Test
    public void getAllUsers_AdminAuthenticated_ShouldReturn200() throws Exception {
        String token = authClient.login("admin", "$2a$10$2529eBq3R6Y41t03Mku2I.2Nh3W0p25lt.s.85mG0kiAvxI4bsAHa");
        //create 2 users
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("testuser")
                .password("Test1234")
                .email("testuser@example.com")
                .role(Role.CUSTOMER)
                .build();
        mockMvc.perform(post("/v1/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        RegisterRequestDTO request2 = RegisterRequestDTO.builder()
                .username("testuser2")
                .password("Test12345")
                .email("testuser2@example.com")
                .role(Role.CUSTOMER)
                .build();
        mockMvc.perform(post("/v1/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)));

        // test the get all users
        mockMvc.perform(get("/v1/users/getAll")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Cleanup
        mockMvc.perform(delete("/v1/users/username/{username}", "testuser")
                        .header("Authorization", "Bearer " + token));

        mockMvc.perform(delete("/v1/users/username/{username}", "testuser2")
                .header("Authorization", "Bearer " + token));
    }

    // TC-14: Register a user with invalid password (missing number)
    @Test
    public void createUser_InvalidPassword_ShouldReturn400() throws Exception {
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("testuser")
                .password("TestPassword") // Invalid password (missing number)
                .email("testuser@example.com")
                .role(Role.CUSTOMER)
                .build();

        mockMvc.perform(post("/v1/users/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("Password must be at least 8 characters and contain at least one letter and one number"));
    }


    // TC-15: Retrieve user information by username
    @Test
    public void getUserByUsername_ValidUsername_ShouldReturn200() throws Exception {
        String token = authClient.login("admin", "$2a$10$2529eBq3R6Y41t03Mku2I.2Nh3W0p25lt.s.85mG0kiAvxI4bsAHa");
        //create a user
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("testuser")
                .password("Test1234")
                .email("testuser@example.com")
                .role(Role.CUSTOMER)
                .build();
        mockMvc.perform(post("/v1/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // test the get user by username
        mockMvc.perform(get("/v1/users/getUserByUsername/{username}", "testuser")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("testuser@example.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));

        // Cleanup
        mockMvc.perform(delete("/v1/users/username/{username}", "testuser")
                .header("Authorization", "Bearer " + token));
    }

    // TC-16: Attempt to delete a user by username by a non-admin (not matching principal)
    @Test
    public void deleteUserByUsername_NonAdminNotMatchingUser_ShouldReturn401() throws Exception {
        String correctToken = authClient.login("admin", "$2a$10$2529eBq3R6Y41t03Mku2I.2Nh3W0p25lt.s.85mG0kiAvxI4bsAHa");
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("testuser")
                .password("Test1234")
                .email("testuser@example.com")
                .role(Role.CUSTOMER)
                .build();

        mockMvc.perform(post("/v1/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));


        String token = "wrong token";
        // Attempt to delete the user with a non-admin token
        mockMvc.perform(delete("/v1/users/username/{username}", "testuser")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());

        // Cleanup
        mockMvc.perform(delete("/v1/users/username/{username}", "testuser")
                        .header("Authorization", "Bearer " + correctToken));
    }

    // TC-17: Create a user with an already existing email
    @Test
    public void createUser_ExistingEmail_ShouldReturn400() throws Exception {
        String correctToken = authClient.login("admin", "$2a$10$2529eBq3R6Y41t03Mku2I.2Nh3W0p25lt.s.85mG0kiAvxI4bsAHa");
        RegisterRequestDTO request1 = RegisterRequestDTO.builder()
                .username("newuser")
                .password("Test1234")
                .email("testuser@example.com")
                .role(Role.CUSTOMER)
                .build();

        RegisterRequestDTO request2 = RegisterRequestDTO.builder()
                .username("newuser2")
                .password("Test12345")
                .email("testuser@example.com")
                .role(Role.CUSTOMER)
                .build();

        mockMvc.perform(post("/v1/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)));

        mockMvc.perform(post("/v1/users/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest());

        // Cleanup
        mockMvc.perform(delete("/v1/users/username/{username}", "newuser")
                .header("Authorization", "Bearer " + correctToken));
    }

    // TC-18: Delete a user by username as an admin
    @Test
    public void deleteUserByUsername_AdminOrMatchingUser_ShouldReturn200() throws Exception {
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("testuser")
                .password("Test1234")
                .email("testuser@example.com")
                .role(Role.CUSTOMER)
                .build();

        mockMvc.perform(post("/v1/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Cleanup and test with admin token
        String token = authClient.login("admin", "$2a$10$2529eBq3R6Y41t03Mku2I.2Nh3W0p25lt.s.85mG0kiAvxI4bsAHa");
        mockMvc.perform(delete("/v1/users/username/{username}", "testuser")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    // TC-19: Retrieve all users as a non-admin
    @Test
    public void getAllUsers_NonAdmin_ShouldReturn401() throws Exception {
        String token = "non_admin_token (guest)";
        String correctToken = authClient.login("admin", "$2a$10$2529eBq3R6Y41t03Mku2I.2Nh3W0p25lt.s.85mG0kiAvxI4bsAHa");
        //create 2 users
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("testuser")
                .password("Test1234")
                .email("testuser@example.com")
                .role(Role.CUSTOMER)
                .build();
        mockMvc.perform(post("/v1/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        RegisterRequestDTO request2 = RegisterRequestDTO.builder()
                .username("testuser2")
                .password("Test12345")
                .email("testuser2@example.com")
                .role(Role.CUSTOMER)
                .build();
        mockMvc.perform(post("/v1/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)));

        // test the get all users
        mockMvc.perform(get("/v1/users/getAll")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());

        // Cleanup
        mockMvc.perform(delete("/v1/users/username/{username}", "testuser")
                .header("Authorization", "Bearer " + correctToken));

        mockMvc.perform(delete("/v1/users/username/{username}", "testuser2")
                .header("Authorization", "Bearer " + correctToken));
    }
}
