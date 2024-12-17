package com.bitescout.app.rankingservice.repository;

import com.bitescout.app.rankingservice.entity.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, UUID> {
    Ranking findByRestaurantId(UUID restaurantId);
}

