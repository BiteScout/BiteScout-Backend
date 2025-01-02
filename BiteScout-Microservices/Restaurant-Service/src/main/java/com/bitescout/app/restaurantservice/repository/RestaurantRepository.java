package com.bitescout.app.restaurantservice.repository;

import com.bitescout.app.restaurantservice.entity.Restaurant;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
    List<Restaurant> findByOwnerId(UUID ownerId);

    @Query(value = "SELECT * FROM restaurants r WHERE ST_DWithin(r.location, ST_MakePoint(:longitude, :latitude)::geography, :radius)", nativeQuery = true)
    List<Restaurant> findByLocationNear(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radiusInMeters
    );
    List<Restaurant> findByNameContainingIgnoreCase(String query);
    Restaurant findByName(String name);

    List<Restaurant> findByCuisineTypeContainingIgnoreCase(String cuisineType);
    List<Restaurant> findByPriceRangeIgnoreCase(String priceRange);
}

