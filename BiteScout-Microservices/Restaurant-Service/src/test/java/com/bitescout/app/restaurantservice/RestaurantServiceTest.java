package com.bitescout.app.restaurantservice;

import com.bitescout.app.restaurantservice.client.AuthClient;
import com.bitescout.app.restaurantservice.dto.*;
import com.bitescout.app.restaurantservice.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
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
public class RestaurantServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthClient authClient;

    private static final GeometryFactory geometry_factory = new GeometryFactory();

    //TC-R01 Create Restaurant with valid data
    @DisplayName("TC-R01 Create Restaurant with valid data")
    @Test
    public void createRestaurantWithValidData_ShouldReturn201() throws Exception {
        String token = authClient.login("admin", "admin1234");
        String jsonRequest = """
        {
            "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
            "name": "Test Restaurant",
            "description": "Test Description",
            "menu": "Menu",
            "location": {
                "type": "Point",
                "coordinates": [12.4924, 41.8902]
            },
            "cuisineType": "Italian",
            "priceRange": "$$"
        }
    """;

        // perform post request
        mockMvc.perform(post("/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());

        //fetching the restaurantId
        String restaurantId = mockMvc.perform(get("/v1/restaurants/getRestaurantId/{restaurantName}", "Test Restaurant")
                .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();

        //Cleanup after test
        mockMvc.perform(delete("/v1/restaurants/{restaurantId}", restaurantId)
                .header("Authorization", "Bearer " + token));

    }

    //TC-R02 Attempt to create a restaurant with missing name.
    @DisplayName("TC-R02 Attempt to create a restaurant with missing name")
    @Test
    public void createRestaurantWithMissingName_ShouldReturn400() throws Exception {
        String token = authClient.login("admin", "admin1234");
        String jsonRequest = """
        {
            "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
            "description": "Test Description",
            "menu": "Menu",
            "location": {
                "type": "Point",
                "coordinates": [12.4924, 41.8902]
            },
            "cuisineType": "Italian",
            "priceRange": "$$"
        }
    """;

        // perform post request
        mockMvc.perform(post("/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    //TC-R03 Retrieve all restaurants.
    @DisplayName("TC-R03 Retrieve all restaurants")
    @Test
    public void retrieveAllRestaurants_ShouldReturn200() throws Exception {

        // perform get request
        mockMvc.perform(get("/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    //TC-R04 Retrieve a restaurant by its ID.
    @DisplayName("TC-R04 Retrieve a restaurant by its ID")
    @Test
    public void retrieveRestaurantById_ShouldReturn200() throws Exception {
        //Create a restaurant to retrieve
        String token = authClient.login("admin", "admin1234");
        String jsonRequest = """
        {
            "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
            "name": "Test Restaurant",
            "description": "Test Description",
            "menu": "Menu",
            "location": {
                "type": "Point",
                "coordinates": [12.4924, 41.8902]
            },
            "cuisineType": "Italian",
            "priceRange": "$$"
        }
    """;

        // perform post request
        mockMvc.perform(post("/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header("Authorization", "Bearer " + token));

        //fetching the restaurantId
        String restaurantId = mockMvc.perform(get("/v1/restaurants/getRestaurantId/{restaurantName}", "Test Restaurant")
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();

        // perform get request
        mockMvc.perform(get("/v1/restaurants/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //Cleanup after test
        mockMvc.perform(delete("/v1/restaurants/{restaurantId}", restaurantId)
                .header("Authorization", "Bearer " + token));

    }

    //TC-R05 Attempt to retrieve a non-existent restaurant by its ID.
    @DisplayName("TC-R05 Attempt to retrieve a non-existent restaurant by its ID")
    @Test
    public void retrieveNonExistentRestaurantById_ShouldReturn404() throws Exception {
        // perform get request
        mockMvc.perform(get("/v1/restaurants/{restaurantId}", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    //TC-R06 Retrieve all restaurants by owner ID.
    @DisplayName("TC-R06 Retrieve all restaurants by owner ID")
    @Test
    public void retrieveAllRestaurantsByOwnerId_ShouldReturn200() throws Exception {
        //Create a restaurant to retrieve
        String token = authClient.login("admin", "admin1234");
        String jsonRequest1 = """
        {
            "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
            "name": "Test Restaurant",
            "description": "Test Description",
            "menu": "Menu",
            "location": {
                "type": "Point",
                "coordinates": [12.4924, 41.8902]
            },
            "cuisineType": "Italian",
            "priceRange": "$$"
        }
    """;
        String jsonRequest2 = """
        {
            "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
            "name": "Test Restaurant 2",
            "description": "Test Description",
            "menu": "Menu",
            "location": {
                "type": "Point",
                "coordinates": [12.4922, 41.8905]
            },
            "cuisineType": "Japanese",
            "priceRange": "$$"
        }
    """;

        // perform post requests to create restaurants for the owner
        mockMvc.perform(post("/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest1)
                        .header("Authorization", "Bearer " + token));
        mockMvc.perform(post("/v1/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest2)
                .header("Authorization", "Bearer " + token));

        // perform get request
        mockMvc.perform(get("/v1/restaurants/owner/{ownerId}", "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //Cleanup after test
        mockMvc.perform(delete("/v1/restaurants/deleteAllRestaurants")
                .header("Authorization", "Bearer " + token));
    }

    //TC-R07 Update a restaurant with valid inputs.
    @DisplayName("TC-R07 Update a restaurant with valid inputs")
    @Test
    public void updateRestaurantWithValidInputs_ShouldReturn200() throws Exception {
        //Create a restaurant to update
        String token = authClient.login("admin", "admin1234");
        String jsonRequest = """
        {
            "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
            "name": "Test Restaurant",
            "description": "Test Description",
            "menu": "Menu",
            "location": {
                "type": "Point",
                "coordinates": [12.4924, 41.8902]
            },
            "cuisineType": "Italian",
            "priceRange": "$$"
        }
    """;

        // perform post request
        mockMvc.perform(post("/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header("Authorization", "Bearer " + token));

        //fetching the restaurantId
        String restaurantId = mockMvc.perform(get("/v1/restaurants/getRestaurantId/{restaurantName}", "Test Restaurant")
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();

        String jsonUpdateRequest = """
        {
            "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
            "name": "Updated Restaurant",
            "description": "Updated Description",
            "menu": "Updated Menu",
            "location": {
                "type": "Point",
                "coordinates": [12.4924, 41.8902]
            },
            "cuisineType": "Indian",
            "priceRange": "$$$"
        }
    """;

        // perform put request
        mockMvc.perform(put("/v1/restaurants/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUpdateRequest)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        //Cleanup after test
        mockMvc.perform(delete("/v1/restaurants/deleteAllRestaurants")
                .header("Authorization", "Bearer " + token));
    }

    //TC-R08 Attempt to update a restaurant with non-matching owner or non-admin.
    @DisplayName("TC-R08 Attempt to update a restaurant with non-matching owner or non-admin.")
    @Test
    public void updateRestaurantWithNonMatchingOwnerId_ShouldReturn403() throws Exception {
        //Create a restaurant to update
        String token = authClient.login("admin", "admin1234");
        String wrong_token = "wrong_token";
        String jsonRequest = """
        {
            "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
            "name": "Test Restaurant",
            "description": "Test Description",
            "menu": "Menu",
            "location": {
                "type": "Point",
                "coordinates": [12.4924, 41.8902]
            },
            "cuisineType": "Italian",
            "priceRange": "$$"
        }
    """;

        // perform post request
        mockMvc.perform(post("/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header("Authorization", "Bearer " + token));

        //fetching the restaurantId
        String restaurantId = mockMvc.perform(get("/v1/restaurants/getRestaurantId/{restaurantName}", "Test Restaurant")
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();

        String jsonUpdateRequest = """
        {
            "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
            "name": "Updated Restaurant",
            "description": "Updated Description",
            "menu": "Updated Menu",
            "location": {
                "type": "Point",
                "coordinates": [12.4924, 41.8902]
            },
            "cuisineType": "Indian",
            "priceRange": "$$$"
        }
    """;

        // perform put request
        mockMvc.perform(put("/v1/restaurants/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUpdateRequest)
                        .header("Authorization", "Bearer " + wrong_token))
                .andExpect(status().isUnauthorized());

        //Cleanup after test
        mockMvc.perform(delete("/v1/restaurants/deleteAllRestaurants")
                .header("Authorization", "Bearer " + token));
    }

    //TC-R09 Retrieve restaurants near a specific location.
    @DisplayName("TC-R09 Retrieve restaurants near a specific location")
    @Test
    public void retrieveRestaurantsNearSpecificLocation_ShouldReturn200() throws Exception {
        //Create a restaurant to retrieve
        String token = authClient.login("admin", "admin1234");
        String jsonRequest1 = """
        {
            "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
            "name": "Test Restaurant",
            "description": "Test Description",
            "menu": "Menu",
            "location": {
                "type": "Point",
                "coordinates": [12.4924, 41.8902]
            },
            "cuisineType": "Italian",
            "priceRange": "$$"
        }
    """;
        String jsonRequest2 = """
        {
            "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
            "name": "Test Restaurant 2",
            "description": "Test Description",
            "menu": "Menu",
            "location": {
                "type": "Point",
                "coordinates": [12.4922, 41.8905]
            },
            "cuisineType": "Japanese",
            "priceRange": "$$"
        }
    """;

        // perform post requests to create restaurants for the owner
        mockMvc.perform(post("/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest1)
                        .header("Authorization", "Bearer " + token));
        mockMvc.perform(post("/v1/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest2)
                .header("Authorization", "Bearer " + token));

        // perform get request
        mockMvc.perform(get("/v1/restaurants/near-me?latitude=41.8902&longitude=12.4924&radius=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //Cleanup after test
        mockMvc.perform(delete("/v1/restaurants/deleteAllRestaurants")
                .header("Authorization", "Bearer " + token));
    }

    //TC-R10 Search for restaurants by name.
    @DisplayName("TC-R10 Search for restaurants by name")
    @Test
    public void searchRestaurantsByName_ShouldReturn200() throws Exception {
        //Create a restaurant to search
        String token = authClient.login("admin", "admin1234");
        String jsonRequest = """
        {
            "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
            "name": "Test Restaurant",
            "description": "Test Description",
            "menu": "Menu",
            "location": {
                "type": "Point",
                "coordinates": [12.4924, 41.8902]
            },
            "cuisineType": "Italian",
            "priceRange": "$$"
        }
    """;

        // perform post request
        mockMvc.perform(post("/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header("Authorization", "Bearer " + token));

        // perform get request
        mockMvc.perform(get("/v1/restaurants/search?restaurantName=Test Restaurant")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Restaurant"));

        //Cleanup after test
        mockMvc.perform(delete("/v1/restaurants/deleteAllRestaurants")
                .header("Authorization", "Bearer " + token));
    }

    //TC-R11 Retrieve restaurants by cuisine type.
    @DisplayName("TC-R11 Retrieve restaurants by cuisine type")
    @Test
    public void retrieveRestaurantsByCuisineType_ShouldReturn200() throws Exception {
        //Create a restaurant to retrieve
        String token = authClient.login("admin", "admin1234");
        String jsonRequest1 = """
        {
            "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
            "name": "Test Restaurant",
            "description": "Test Description",
            "menu": "Menu",
            "location": {
                "type": "Point",
                "coordinates": [12.4924, 41.8902]
            },
            "cuisineType": "Italian",
            "priceRange": "$$"
        }
    """;

        // perform post requests to create restaurants for the owner
        mockMvc.perform(post("/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest1)
                        .header("Authorization", "Bearer " + token));

        // perform get request
        mockMvc.perform(get("/v1/restaurants/cuisine?cuisineType=Italian")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cuisineType").value("Italian"));

        //Cleanup after test
        mockMvc.perform(delete("/v1/restaurants/deleteAllRestaurants")
                .header("Authorization", "Bearer " + token));
    }

    //TC-R12 Retrieve restaurants by price range.
    @DisplayName("TC-R12 Retrieve restaurants by price range")
    @Test
    public void retrieveRestaurantsByPriceRange_ShouldReturn200() throws Exception {
        //Create a restaurant to retrieve
        String token = authClient.login("admin", "admin1234");
        String jsonRequest1 = """
        {
            "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
            "name": "Test Restaurant",
            "description": "Test Description",
            "menu": "Menu",
            "location": {
                "type": "Point",
                "coordinates": [12.4924, 41.8902]
            },
            "cuisineType": "Italian",
            "priceRange": "$$"
        }
    """;

        // perform post requests to create restaurants for the owner
        mockMvc.perform(post("/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest1)
                        .header("Authorization", "Bearer " + token));

        // perform get request
        mockMvc.perform(get("/v1/restaurants/price?priceRange=$$")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].priceRange").value("$$"));

        //Cleanup after test
        mockMvc.perform(delete("/v1/restaurants/deleteAllRestaurants")
                .header("Authorization", "Bearer " + token));
    }

    //TC-R13 Delete a restaurant as an admin or ownership.
    @DisplayName("TC-R13 Delete a restaurant as an admin or ownership")
    @Test
    public void deleteRestaurantAsAdminOrOwnership_ShouldReturn204() throws Exception {
        //Create a restaurant to delete
        String token = authClient.login("admin", "admin1234");
        String jsonRequest = """
        {
            "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
            "name": "Test Restaurant",
            "description": "Test Description",
            "menu": "Menu",
            "location": {
                "type": "Point",
                "coordinates": [12.4924, 41.8902]
            },
            "cuisineType": "Italian",
            "priceRange": "$$"
        }
    """;

        // perform post request
        mockMvc.perform(post("/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header("Authorization", "Bearer " + token));

        //fetching the restaurantId
        String restaurantId = mockMvc.perform(get("/v1/restaurants/getRestaurantId/{restaurantName}", "Test Restaurant")
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();

        // perform delete request
        mockMvc.perform(delete("/v1/restaurants/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    //TC-R14 Attempt to delete a restaurant as a non-admin without ownership.
    @DisplayName("TC-R14 Attempt to delete a restaurant as a non-admin without ownership")
    @Test
    public void deleteRestaurantAsNonAdminWithoutOwnership_ShouldReturn403() throws Exception {
        //Create a restaurant to delete
        String token = authClient.login("admin", "admin1234");
        String wrong_token = "wrong_token";
        String jsonRequest = """
        {
            "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
            "name": "Test Restaurant",
            "description": "Test Description",
            "menu": "Menu",
            "location": {
                "type": "Point",
                "coordinates": [12.4924, 41.8902]
            },
            "cuisineType": "Italian",
            "priceRange": "$$"
        }
    """;

        // perform post request
        mockMvc.perform(post("/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header("Authorization", "Bearer " + token));

        //fetching the restaurantId
        String restaurantId = mockMvc.perform(get("/v1/restaurants/getRestaurantId/{restaurantName}", "Test Restaurant")
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();

        // perform delete request
        mockMvc.perform(delete("/v1/restaurants/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + wrong_token))
                .andExpect(status().isUnauthorized());

        //Cleanup after test
        mockMvc.perform(delete("/v1/restaurants/deleteAllRestaurants")
                .header("Authorization", "Bearer " + token));
    }

    //TC-R15 Update a restaurant's menu as an admin or owner.
    @DisplayName("TC-R15 Update a restaurant's menu as an admin or owner")
    @Test
    public void updateRestaurantMenuAsAdminOrOwner_ShouldReturn200() throws Exception {
        //Create a restaurant to update menu
        String token = authClient.login("admin", "admin1234");
        String jsonRequest = """
        {
            "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
            "name": "Test Restaurant",
            "description": "Test Description",
            "menu": "menulink.com",
            "location": {
                "type": "Point",
                "coordinates": [12.4924, 41.8902]
            },
            "cuisineType": "Italian",
            "priceRange": "$$"
        }
    """;
        // perform post request
        mockMvc.perform(post("/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header("Authorization", "Bearer " + token));

        //fetching the restaurantId
        String restaurantId = mockMvc.perform(get("/v1/restaurants/getRestaurantId/{restaurantName}", "Test Restaurant")
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();

        // perform put request and update menu is a string as parameter
        mockMvc.perform(put("/v1/restaurants/{restaurantId}/update-menu?menu=updatedmenu.com", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        //Cleanup after test
        mockMvc.perform(delete("/v1/restaurants/deleteAllRestaurants")
                .header("Authorization", "Bearer " + token));
    }

    //TC-R16 Create a special offer for a restaurant as an admin or owner.
    @DisplayName("TC-R16 Create a special offer for a restaurant as an admin or owner")
    @Test
    public void createSpecialOfferForRestaurantAsAdminOrOwner_ShouldReturn201() throws Exception {
        //Create a restaurant to create special offer
        String token = authClient.login("admin", "admin1234");
        String jsonRequest = """
                    {
                        "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
                        "name": "Test Restaurant",
                        "description": "Test Description",
                        "menu": "menulink.com",
                        "location": {
                            "type": "Point",
                            "coordinates": [12.4924, 41.8902]
                        },
                        "cuisineType": "Italian",
                        "priceRange": "$$"
                    }
                """;
        // perform post request
        mockMvc.perform(post("/v1/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .header("Authorization", "Bearer " + token));

        //fetching the restaurantId
        String restaurantId = mockMvc.perform(get("/v1/restaurants/getRestaurantId/{restaurantName}", "Test Restaurant")
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();

        String jsonSpecialOfferRequest = """
                    {
                        "title": "Special Offer",
                        "description": "Special Offer Description",
                        "startDate": "2021-12-01",
                        "endDate": "2021-12-31"
                    }
                """;

        // perform post request
        mockMvc.perform(post("/v1/restaurants/{restaurantId}/offers", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSpecialOfferRequest)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());

        //Cleanup after test
        mockMvc.perform(delete("/v1/restaurants/deleteAllOffers")
                .header("Authorization", "Bearer " + token));
        mockMvc.perform(delete("/v1/restaurants/deleteAllRestaurants")
                .header("Authorization", "Bearer " + token));
    }


    //TC-R17 Retrieve all special offers for a specific restaurant.
    @DisplayName("TC-R17 Retrieve all special offers for a specific restaurant")
    @Test
    public void retrieveAllSpecialOffersForRestaurant_ShouldReturn200() throws Exception {
        //Create a restaurant to create special offer
        String token = authClient.login("admin", "admin1234");
        String jsonRequest = """
                    {
                        "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
                        "name": "Test Restaurant",
                        "description": "Test Description",
                        "menu": "menulink.com",
                        "location": {
                            "type": "Point",
                            "coordinates": [12.4924, 41.8902]
                        },
                        "cuisineType": "Italian",
                        "priceRange": "$$"
                    }
                """;
        // perform post request
        mockMvc.perform(post("/v1/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .header("Authorization", "Bearer " + token));

        //fetching the restaurantId
        String restaurantId = mockMvc.perform(get("/v1/restaurants/getRestaurantId/{restaurantName}", "Test Restaurant")
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();

        String jsonSpecialOfferRequest1 = """
                    {
                        "title": "Special Offer",
                        "description": "Special Offer Description",
                        "startDate": "2021-12-01",
                        "endDate": "2021-12-31"
                    }
                """;
        String jsonSpecialOfferRequest2 = """
                    {
                        "title": "Special Offer 2",
                        "description": "Special Offer Description 2",
                        "startDate": "2021-12-05",
                        "endDate": "2021-12-25"
                    }
                """;

        // perform post request
        mockMvc.perform(post("/v1/restaurants/{restaurantId}/offers", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSpecialOfferRequest1)
                        .header("Authorization", "Bearer " + token));
        mockMvc.perform(post("/v1/restaurants/{restaurantId}/offers", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSpecialOfferRequest2)
                        .header("Authorization", "Bearer " + token));


        // perform get request
        mockMvc.perform(get("/v1/restaurants/{restaurantId}/offers", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Special Offer"))
                .andExpect(jsonPath("$[1].title").value("Special Offer 2"));

        //Cleanup after test
        mockMvc.perform(delete("/v1/restaurants/deleteAllOffers")
                .header("Authorization", "Bearer " + token));
        mockMvc.perform(delete("/v1/restaurants/deleteAllRestaurants")
                .header("Authorization", "Bearer " + token));
    }

    //TC-R18 Update a special offer for a restaurant as an admin or owner.
    @DisplayName("TC-R18 Update a special offer for a restaurant as an admin or owner")
    @Test
    public void updateSpecialOfferForRestaurantAsAdminOrOwner_ShouldReturn200() throws Exception {
        //Create a restaurant to create special offer
        String token = authClient.login("admin", "admin1234");
        String jsonRequest = """
                    {
                        "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
                        "name": "Test Restaurant",
                        "description": "Test Description",
                        "menu": "menulink.com",
                        "location": {
                            "type": "Point",
                            "coordinates": [12.4924, 41.8902]
                        },
                        "cuisineType": "Italian",
                        "priceRange": "$$"
                    }
                """;
        // perform post request
        mockMvc.perform(post("/v1/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .header("Authorization", "Bearer " + token));

        //fetching the restaurantId
        String restaurantId = mockMvc.perform(get("/v1/restaurants/getRestaurantId/{restaurantName}", "Test Restaurant")
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();

        String jsonSpecialOfferRequest = """
                    {
                        "title": "Special Offer",
                        "description": "Special Offer Description",
                        "startDate": "2021-12-01",
                        "endDate": "2021-12-31"
                    }
                """;

        // perform post request
        mockMvc.perform(post("/v1/restaurants/{restaurantId}/offers", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSpecialOfferRequest)
                        .header("Authorization", "Bearer " + token));

        //fetching the specialOfferId
        String specialOfferId = mockMvc.perform(get("/v1/restaurants/getOfferId/{restaurantName}/offers/{title}","Test Restaurant", "Special Offer")
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();

        String jsonUpdateSpecialOfferRequest = """
                    {
                        "title": "Updated Special Offer",
                        "description": "Updated Special Offer Description",
                        "startDate": "2021-12-01",
                        "endDate": "2021-12-31"
                    }
                """;

        // perform put request
        mockMvc.perform(put("/v1/restaurants/{restaurantId}/offers/{offerId}", restaurantId, specialOfferId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUpdateSpecialOfferRequest)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Special Offer"));

        //Cleanup after test
        mockMvc.perform(delete("/v1/restaurants/deleteAllOffers")
                .header("Authorization", "Bearer " + token));
        mockMvc.perform(delete("/v1/restaurants/deleteAllRestaurants")
                .header("Authorization", "Bearer " + token));
    }

    //TC-R19 Attempt to update a special offer as a non-admin without ownership.
    @DisplayName("TC-R19 Attempt to update a special offer as a non-admin without ownership")
    @Test
    public void updateSpecialOfferAsNonAdminWithoutOwnership_ShouldReturn403() throws Exception {
        //Create a restaurant to create special offer
        String token = authClient.login("admin", "admin1234");
        String wrong_token = "wrong_token";
        String jsonRequest = """
                    {
                        "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
                        "name": "Test Restaurant",
                        "description": "Test Description",
                        "menu": "menulink.com",
                        "location": {
                            "type": "Point",
                            "coordinates": [12.4924, 41.8902]
                        },
                        "cuisineType": "Italian",
                        "priceRange": "$$"
                    }
                """;
        // perform post request
        mockMvc.perform(post("/v1/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .header("Authorization", "Bearer " + token));

        //fetching the restaurantId
        String restaurantId = mockMvc.perform(get("/v1/restaurants/getRestaurantId/{restaurantName}", "Test Restaurant")
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();

        String jsonSpecialOfferRequest = """
                    {
                        "title": "Special Offer",
                        "description": "Special Offer Description",
                        "startDate": "2021-12-01",
                        "endDate": "2021-12-31"
                    }
                """;

        // perform post request
        mockMvc.perform(post("/v1/restaurants/{restaurantId}/offers", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSpecialOfferRequest)
                        .header("Authorization", "Bearer " + token));

        //fetching the specialOfferId
        String specialOfferId = mockMvc.perform(get("/v1/restaurants/getOfferId/{restaurantName}/offers/{title}","Test Restaurant", "Special Offer")
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();

        String jsonUpdateSpecialOfferRequest = """
                    {
                        "title": "Updated Special Offer",
                        "description": "Updated Special Offer Description",
                        "startDate": "2021-12-01",
                        "endDate": "2021-12-31"
                    }
                """;

        // perform put request
        mockMvc.perform(put("/v1/restaurants/{restaurantId}/offers/{offerId}", restaurantId, specialOfferId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUpdateSpecialOfferRequest)
                        .header("Authorization", "Bearer " + wrong_token))
                .andExpect(status().isUnauthorized());

        //Cleanup after test
        mockMvc.perform(delete("/v1/restaurants/deleteAllOffers")
                .header("Authorization", "Bearer " + token));
        mockMvc.perform(delete("/v1/restaurants/deleteAllRestaurants")
                .header("Authorization", "Bearer " + token));
    }

    //TC-R20 Delete a special offer for a restaurant as an admin or owner.
    @DisplayName("TC-R20 Delete a special offer for a restaurant as an admin or owner")
    @Test
    public void deleteSpecialOfferForRestaurantAsAdminOrOwner_ShouldReturn204() throws Exception {
        //Create a restaurant to create special offer
        String token = authClient.login("admin", "admin1234");
        String jsonRequest = """
                    {
                        "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
                        "name": "Test Restaurant",
                        "description": "Test Description",
                        "menu": "menulink.com",
                        "location": {
                            "type": "Point",
                            "coordinates": [12.4924, 41.8902]
                        },
                        "cuisineType": "Italian",
                        "priceRange": "$$"
                    }
                """;
        // perform post request
        mockMvc.perform(post("/v1/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .header("Authorization", "Bearer " + token));

        //fetching the restaurantId
        String restaurantId = mockMvc.perform(get("/v1/restaurants/getRestaurantId/{restaurantName}", "Test Restaurant")
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();

        String jsonSpecialOfferRequest = """
                    {
                        "title": "Special Offer",
                        "description": "Special Offer Description",
                        "startDate": "2021-12-01",
                        "endDate": "2021-12-31"
                    }
                """;

        // perform post request
        mockMvc.perform(post("/v1/restaurants/{restaurantId}/offers", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSpecialOfferRequest)
                        .header("Authorization", "Bearer " + token));

        //fetching the specialOfferId
        String specialOfferId = mockMvc.perform(get("/v1/restaurants/getOfferId/{restaurantName}/offers/{title}","Test Restaurant", "Special Offer")
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();

        // perform delete request
        mockMvc.perform(delete("/v1/restaurants/{restaurantId}/offers/{offerId}", restaurantId, specialOfferId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        //Cleanup after test
        mockMvc.perform(delete("/v1/restaurants/deleteAllOffers")
                .header("Authorization", "Bearer " + token));
        mockMvc.perform(delete("/v1/restaurants/deleteAllRestaurants")
                .header("Authorization", "Bearer " + token));
    }

    //TC-R21 Attempt to delete a special offer as a non-admin without ownership.
    @DisplayName("TC-R21 Attempt to delete a special offer as a non-admin without ownership")
    @Test
    public void deleteSpecialOfferAsNonAdminWithoutOwnership_ShouldReturn403() throws Exception {
        //Create a restaurant to create special offer
        String token = authClient.login("admin", "admin1234");
        String wrong_token = "wrong_token";
        String jsonRequest = """
                    {
                        "ownerId": "4b8e6db8-2b9e-4204-85e8-ff0bd082b9d7",
                        "name": "Test Restaurant",
                        "description": "Test Description",
                        "menu": "menulink.com",
                        "location": {
                            "type": "Point",
                            "coordinates": [12.4924, 41.8902]
                        },
                        "cuisineType": "Italian",
                        "priceRange": "$$"
                    }
                """;
        // perform post request
        mockMvc.perform(post("/v1/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .header("Authorization", "Bearer " + token));

        //fetching the restaurantId
        String restaurantId = mockMvc.perform(get("/v1/restaurants/getRestaurantId/{restaurantName}", "Test Restaurant")
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();

        String jsonSpecialOfferRequest = """
                    {
                        "title": "Special Offer",
                        "description": "Special Offer Description",
                        "startDate": "2021-12-01",
                        "endDate": "2021-12-31"
                    }
                """;

        // perform post request
        mockMvc.perform(post("/v1/restaurants/{restaurantId}/offers", restaurantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSpecialOfferRequest)
                .header("Authorization", "Bearer " + token));

        //fetching the specialOfferId
        String specialOfferId = mockMvc.perform(get("/v1/restaurants/getOfferId/{restaurantName}/offers/{title}", "Test Restaurant", "Special Offer")
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();

        // perform delete request
        mockMvc.perform(delete("/v1/restaurants/{restaurantId}/offers/{offerId}", restaurantId, specialOfferId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + wrong_token))
                .andExpect(status().isUnauthorized());

        //Cleanup after test
        mockMvc.perform(delete("/v1/restaurants/deleteAllOffers")
                .header("Authorization", "Bearer " + token));
        mockMvc.perform(delete("/v1/restaurants/deleteAllRestaurants")
                .header("Authorization", "Bearer " + token));
    }

}