package com.bitescout.app.rankingservice.client;

import com.bitescout.app.restaurantservice.util.PointDeserializer;
import com.bitescout.app.restaurantservice.util.PointSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDto {
    private UUID id;
    private String ownerId;
    private String name;
    private String description;
    private String menu;
    private String cuisineType;
    private String priceRange;
    private String createdAt;
    private String updatedAt;
}