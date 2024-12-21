package com.bitescout.app.rankingservice.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDto {
    private String id;
    private String ownerId;
    private String name;
    private String description;
    private String menu;
    private String cuisineType;
    private Point location;
    private String priceRange;
    private String createdAt;
    private String updatedAt;
}