package com.bitescout.app.rankingservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor  // Generates a constructor with all fields
public class RankingRequest {
    private final String restaurantName;
    private final TierRanking tierRanking;
    private final double rating;
    private final int totalReviews;
    private final double popularityScore;
}
