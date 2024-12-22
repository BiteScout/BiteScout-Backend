package com.bitescout.app.rankingservice.service;

import com.bitescout.app.rankingservice.client.RestaurantClient;
import com.bitescout.app.rankingservice.client.RestaurantDto;
import com.bitescout.app.rankingservice.client.ReviewClient;
import com.bitescout.app.rankingservice.client.ReviewDto;
import com.bitescout.app.rankingservice.entity.Ranking;
import com.bitescout.app.rankingservice.entity.RankingResponse;
import com.bitescout.app.rankingservice.entity.TierRanking;
import com.bitescout.app.rankingservice.mapper.RankingMapper;
import com.bitescout.app.rankingservice.repository.RankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RankingService {
    private final RankingRepository rankingRepository;
    private final ReviewClient reviewClient;
    private final RankingMapper rankingMapper;
    private final RestTemplate restTemplate;
    private final RestaurantClient restaurantClient;

    @Value("${spring.ranking.email.service.url}")
    private String rankingEmailServiceUrl;
    public RankingResponse getRestaurantRating(UUID restaurantId) {
        return rankingMapper.toRankingResponse(rankingRepository.findByRestaurantId(restaurantId));
    }
    public int getRestaurantTotalReviews(UUID restaurantId) {
        return rankingRepository.findByRestaurantId(restaurantId).getTotalReviews();
    }

    public double calculateAverageRating(UUID restaurantId) {

        double rating = reviewClient.getReviewsByRestaurant(restaurantId).getBody().stream()
                .mapToInt(ReviewDto::rating)
                .average()
                .orElse(0.0);

        return rating;
    }

    public double calculatePopularityScore(UUID restaurantId) {
        double rating = rankingRepository.findByRestaurantId(restaurantId).getAverageRating();
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
                .averageRating(average_rating)
                .totalReviews(total_reviews)
                .popularityScore(popularity_score)
                .tierRanking(tierRanking)
                .build());
    }
    public Ranking updateRestaurantRating(UUID restaurantId) {
        double average_rating = calculateAverageRating(restaurantId);
        int total_reviews = reviewClient.getReviewsByRestaurant(restaurantId).getBody().size();
        double popularity_score = calculatePopularityScore(restaurantId);
        TierRanking tierRanking = TierRanking.getTier(average_rating);

        Ranking ranking = rankingRepository.findByRestaurantId(restaurantId);
        ranking.setAverageRating(average_rating);
        ranking.setTotalReviews(total_reviews);
        ranking.setPopularityScore(popularity_score);
        ranking.setTierRanking(tierRanking);
        return rankingRepository.save(ranking);
    }

    public List<RankingResponse> getRanking() {
        List<Ranking> weeklyRanking = rankingRepository.findAll();
        if (weeklyRanking.isEmpty()) {
            throw new RuntimeException("No rankings found");
        }
        if (weeklyRanking.size() > 10) {
            weeklyRanking.sort((r1, r2) -> Double.compare(r2.getPopularityScore(), r1.getPopularityScore()));
            weeklyRanking.subList(0, 10);
        }
        List<Map<String, Object>> cloudResponse = new ArrayList<>();
        for (Ranking ranking : weeklyRanking) {
            RestaurantDto restaurantDto = restaurantClient.getRestaurantById(ranking.getRestaurantId()).getBody();
            Map<String, Object> rankingData = new HashMap<>();
            rankingData.put("Restaurant Name", restaurantDto.getName());
            rankingData.put("Restaurant Cuisine", restaurantDto.getCuisineType());
            rankingData.put("Restaurant Price Range", restaurantDto.getPriceRange());
            rankingData.put("Restaurant Location", restaurantDto.getLocation());
            rankingData.put("Average Rating", ranking.getAverageRating());
            rankingData.put("totalReviews", ranking.getTotalReviews());
            rankingData.put("popularityScore", ranking.getPopularityScore());
            rankingData.put("tierRanking", ranking.getTierRanking());
            cloudResponse.add(rankingData);
        }
        saveToCloud(cloudResponse);

        List<RankingResponse> weeklyRankingResponse = null;
        for (Ranking ranking : weeklyRanking) {
            weeklyRankingResponse.add(rankingMapper.toRankingResponse(ranking));
        }
        return weeklyRankingResponse;
    }
    private void saveToCloud(List<Map<String, Object>> request) {
        HttpEntity<List<Map<String, Object>>> requestEntity = new HttpEntity<>(request);
        restTemplate.postForEntity(rankingEmailServiceUrl + "/upload", requestEntity, Void.class);
    }



}
