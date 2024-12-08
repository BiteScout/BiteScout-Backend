package com.bitescout.app.restaurantservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantRequestDTO {
    private String ownerId;
    private String name;
    private String description;
    private String menu;
    private String cuisineType;
    private String location;
    private String priceRange;
}
