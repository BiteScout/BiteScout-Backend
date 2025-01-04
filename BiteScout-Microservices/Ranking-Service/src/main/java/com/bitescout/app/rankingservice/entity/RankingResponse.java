package com.bitescout.app.rankingservice.entity;

public record RankingResponse(
        double averageRating,
        TierRanking tierRanking,
        double popularityScore
) {

}