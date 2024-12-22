package com.bitescout.app.restaurantservice.dto;

import com.bitescout.app.restaurantservice.util.PointSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponseDTO {
    private UUID id;
    private UUID ownerId;
    private String name;
    private String description;
    private String menu;
    private String cuisineType;

    @JsonSerialize(using = PointSerializer.class)
    private Point location;
    private String priceRange;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
