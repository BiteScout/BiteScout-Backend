package com.bitescout.app.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponseDTO {
    private String id;
    private String userId;
    private String restaurantId;
    private String favoritedAt;
}
