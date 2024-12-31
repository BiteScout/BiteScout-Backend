package com.bitescout.app.restaurantservice.repository;

import com.bitescout.app.restaurantservice.entity.SpecialOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpecialOfferRepository extends JpaRepository<SpecialOffer, UUID> {
    List<SpecialOffer> findAllByRestaurantId(UUID restaurantId);

    SpecialOffer findByRestaurant_NameAndTitle(String restaurantName, String title);
}

