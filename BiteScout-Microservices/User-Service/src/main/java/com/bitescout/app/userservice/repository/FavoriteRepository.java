package com.bitescout.app.userservice.repository;
import com.bitescout.app.userservice.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {

    Optional<Favorite> findByUserAndRestaurantId(User user, UUID restaurantId);
    List<Favorite> findByUser(User user);

    Long countByRestaurantId(UUID uuid);
}
