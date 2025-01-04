package com.bitescout.app.rankingservice.mapper;

import com.bitescout.app.rankingservice.entity.Ranking;
import com.bitescout.app.rankingservice.entity.RankingResponse;
import org.springframework.stereotype.Component;

@Component
public class RankingMapper {
    public RankingResponse toRankingResponse(Ranking ranking) {
        return new RankingResponse(
                ranking.getAverageRating(),
                ranking.getTierRanking(),
                ranking.getPopularityScore()
        );
    }
}
