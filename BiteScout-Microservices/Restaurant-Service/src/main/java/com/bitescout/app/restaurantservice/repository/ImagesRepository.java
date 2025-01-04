package com.bitescout.app.restaurantservice.repository;

import com.bitescout.app.restaurantservice.entity.Images;
import com.bitescout.app.restaurantservice.entity.Restaurant;
import com.bitescout.app.restaurantservice.entity.SpecialOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImagesRepository extends JpaRepository<Images, UUID> {
    List<Images> findAllByRestaurant(Restaurant restaurant);
    void deleteAllByRestaurant(Restaurant restaurant);
}

