package com.bitescout.app.rankingservice.mapper;

import com.bitescout.app.rankingservice.entity.Ranking;
import com.bitescout.app.rankingservice.entity.RankingResponse;
import org.springframework.stereotype.Component;

@Component
public class RankingMapper {
    public RankingResponse toRankingResponse(Ranking ranking) {
        return new RankingResponse(
                ranking.getAverage_rating(),
                ranking.getTierRanking()
        );
    }

    public Ranking toRanking(RankingResponse rankingResponse) {
        return Ranking.builder()
                .average_rating(rankingResponse.averageRating())
                .tierRanking(rankingResponse.tierRanking())
                .build();
    }
}
