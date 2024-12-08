package com.bitescout.app.rankingservice.entity;

public enum TierRanking {
    PLATINUM(4.5, 5.0),
    GOLD(4.0, 4.49),
    SILVER(3.0, 3.99),
    BRONZE(0.0, 2.99);

    private final double minRating;
    private final double maxRating;

    TierRanking(double minRating, double maxRating) {
        this.minRating = minRating;
        this.maxRating = maxRating;
    }

    public static TierRanking getTier(double averageRating) {
        for (TierRanking tier : TierRanking.values()) {
            if (averageRating >= tier.minRating && averageRating <= tier.maxRating) {
                return tier;
            }
        }
        return BRONZE; // Default to BRONZE if no tier matches
    }
}
