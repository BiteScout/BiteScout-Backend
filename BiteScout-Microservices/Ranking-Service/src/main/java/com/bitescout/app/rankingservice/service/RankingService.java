package com.bitescout.app.rankingservice.service;

import com.bitescout.app.rankingservice.client.ReviewClient;
import com.bitescout.app.rankingservice.client.ReviewDto;
import com.bitescout.app.rankingservice.entity.Ranking;
import com.bitescout.app.rankingservice.entity.RankingResponse;
import com.bitescout.app.rankingservice.entity.TierRanking;
import com.bitescout.app.rankingservice.mapper.RankingMapper;
import com.bitescout.app.rankingservice.repository.RankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RankingService {
    private final RankingRepository rankingRepository;
    private final ReviewClient reviewClient;
    private final RankingMapper rankingMapper;

    public RankingResponse getRestaurantRating(UUID restaurantId) {
        return rankingMapper.toRankingResponse(rankingRepository.findByRestaurantId(restaurantId));
    }
    public int getRestaurantTotalReviews(UUID restaurantId) {
        return rankingRepository.findByRestaurantId(restaurantId).getTotal_reviews();
    }

    public double calculateAverageRating(UUID restaurantId) {

        double rating = reviewClient.getReviewsByRestaurant(restaurantId).getBody().stream()
                .mapToInt(ReviewDto::rating)
                .average()
                .orElse(0.0);

        return rating;
    }

    public double calculatePopularityScore(UUID restaurantId) {
        double rating = rankingRepository.getAverageRating(restaurantId);
        int total_reviews = reviewClient.getReviewsByRestaurant(restaurantId).getBody().size();

        return rating * Math.log1p(total_reviews);
    }
    public Ranking submitRestaurantRating(UUID restaurantId) {
       if(rankingRepository.findByRestaurantId(restaurantId) == null) {
           return submitNewRestaurantRating(restaurantId);
       }
         return updateRestaurantRating(restaurantId);
    }
    public Ranking submitNewRestaurantRating(UUID restaurantId) {
        double average_rating = calculateAverageRating(restaurantId);
        int total_reviews = reviewClient.getReviewsByRestaurant(restaurantId).getBody().size();
        double popularity_score = calculatePopularityScore(restaurantId);
        TierRanking tierRanking = TierRanking.getTier(average_rating);
        return rankingRepository.save(Ranking.builder()
                .restaurantId(restaurantId)
                .average_rating(average_rating)
                .total_reviews(total_reviews)
                .popularity_score(popularity_score)
                .tierRanking(tierRanking)
                .build());
    }
    public Ranking updateRestaurantRating(UUID restaurantId) {
        double average_rating = calculateAverageRating(restaurantId);
        int total_reviews = reviewClient.getReviewsByRestaurant(restaurantId).getBody().size();
        double popularity_score = calculatePopularityScore(restaurantId);
        TierRanking tierRanking = TierRanking.getTier(average_rating);

        Ranking ranking = rankingRepository.findByRestaurantId(restaurantId);
        ranking.setAverage_rating(average_rating);
        ranking.setTotal_reviews(total_reviews);
        ranking.setPopularity_score(popularity_score);
        ranking.setTierRanking(tierRanking);

        return rankingRepository.save(ranking);
    }

    public List<RankingResponse> getRatings() {
        List<Ranking> weeklyRanking = rankingRepository.findAll();
        if (weeklyRanking.isEmpty()) {
            throw new RuntimeException("No rankings found");
        }
        if (weeklyRanking.size() > 10) {
            weeklyRanking.sort((r1, r2) -> Double.compare(r2.getPopularity_score(), r1.getPopularity_score()));
            weeklyRanking.subList(0, 10);
        }
        List<RankingResponse> weeklyRankingResponse = null;
        for (Ranking ranking : weeklyRanking) {
            weeklyRankingResponse.add(rankingMapper.toRankingResponse(ranking));
        }
        return weeklyRankingResponse;


    }



}
