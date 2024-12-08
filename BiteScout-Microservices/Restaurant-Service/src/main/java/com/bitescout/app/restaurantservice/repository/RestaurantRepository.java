package com.bitescout.app.restaurantservice.repository;

import com.bitescout.app.restaurantservice.entity.Restaurant;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
    List<Restaurant> findByOwnerId(UUID ownerId);
    List<Restaurant> findByLocationNear(Point location, Distance distance);
    List<Restaurant> findByNameContainingIgnoreCase(String query);

    List<Restaurant> findByCuisineTypeContainingIgnoreCase(String cuisineType);
    List<Restaurant> findByPriceRangeContainingIgnoreCase(String priceRange);
}

