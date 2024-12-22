package com.bitescout.app.restaurantservice.dto;

import com.bitescout.app.restaurantservice.util.PointDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
public class RestaurantRequestDTO {
    private UUID ownerId;
    private String name;
    private String description;
    private String menu;
    private String cuisineType;

    @JsonDeserialize(using = PointDeserializer.class)
    private Point location;

    private String priceRange;
}
