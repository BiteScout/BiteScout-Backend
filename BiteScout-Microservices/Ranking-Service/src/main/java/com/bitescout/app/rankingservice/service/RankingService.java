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
import org.springframework.http.ResponseEntity;
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
    public RankingResponse getRestaurantRating(String restaurantId) {
        return rankingMapper.toRankingResponse(rankingRepository.findByRestaurantId(UUID.fromString(restaurantId)));
    }

    public double calculateAverageRating(String restaurantId) {
        List<ReviewDto> reviews = reviewClient.getReviewsByRestaurant(UUID.fromString(restaurantId)).getBody();
        if (reviews == null || reviews.isEmpty()) {
            return 0.0; // Return 0 if reviews are null or empty
        }
        return reviews.stream()
                .mapToInt(ReviewDto::rating)
                .average()
                .orElse(0.0);
    }

    public double calculatePopularityScore(String restaurantId) {
        Ranking ranking = rankingRepository.findByRestaurantId(UUID.fromString(restaurantId));

        if (ranking == null) {
            // Handle this case appropriately, e.g., return a default value or throw an exception
            return 0.0;
        }
        // Safe to access ranking methods now
        return ranking.getAverageRating() * Math.log1p(ranking.getTotalReviews());    }

    public void submitRestaurantRating() {
        List<RestaurantDto> restaurants = restaurantClient.getRestaurants().getBody();
        // Dereference of 'restaurants' may produce 'NullPointerException' if 'restaurants' is null
        for (RestaurantDto restaurantDto : restaurants) {
            ResponseEntity<List<ReviewDto>> response = reviewClient.getReviewsByRestaurant(restaurantDto.getId());
            List<ReviewDto> reviews = response.getBody();

            if (reviews == null || reviews.isEmpty()) {
                continue; // Skip restaurants with no reviews
            }

            if (rankingRepository.findByRestaurantId(restaurantDto.getId()) == null) {
                submitNewRestaurantRating(restaurantDto.getId().toString());
            } else {
                updateRestaurantRating(restaurantDto.getId().toString());
            }
        }

    }
    public void submitNewRestaurantRating(String restaurantId) {
        double average_rating = calculateAverageRating(restaurantId);
        int total_reviews = reviewClient.getReviewsByRestaurant(UUID.fromString(restaurantId)).getBody().size();
        double popularity_score = calculatePopularityScore((restaurantId));
        TierRanking tierRanking = TierRanking.getTier(average_rating);
        rankingRepository.save(Ranking.builder()
                .restaurantId(UUID.fromString(restaurantId))
                .averageRating(average_rating)
                .totalReviews(total_reviews)
                .popularityScore(popularity_score)
                .tierRanking(tierRanking)
                .build());
    }
    public void updateRestaurantRating(String restaurantId) {
        double average_rating = calculateAverageRating(restaurantId);
        int total_reviews = reviewClient.getReviewsByRestaurant(UUID.fromString(restaurantId)).getBody().size();
        double popularity_score = calculatePopularityScore((restaurantId));
        TierRanking tierRanking = TierRanking.getTier(average_rating);

        Ranking ranking = rankingRepository.findByRestaurantId(UUID.fromString(restaurantId));
        ranking.setAverageRating(average_rating);
        ranking.setTotalReviews(total_reviews);
        ranking.setPopularityScore(popularity_score);
        ranking.setTierRanking(tierRanking);
        rankingRepository.save(ranking);
    }

    public List<RankingResponse> getRanking() {
        List<Ranking> weeklyRanking = rankingRepository.findAll();
        if (weeklyRanking.isEmpty()) {
            throw new RuntimeException("No rankings found");
        }
        if (weeklyRanking.size() > 10) {
            weeklyRanking.sort((r1, r2) -> Double.compare(r2.getPopularityScore(), r1.getPopularityScore()));
            weeklyRanking = weeklyRanking.subList(0, 10);
        }
        List<Map<String, Object>> cloudResponse = new ArrayList<>();
        for (Ranking ranking : weeklyRanking) {
            RestaurantDto restaurantDto = restaurantClient.getRestaurantById(ranking.getRestaurantId()).getBody();
            Map<String, Object> rankingData = new HashMap<>();
            rankingData.put("Restaurant Name", restaurantDto.getName());
            rankingData.put("Restaurant Cuisine", restaurantDto.getCuisineType());
            rankingData.put("Restaurant Price Range", restaurantDto.getPriceRange());
            rankingData.put("Average Rating", ranking.getAverageRating());
            rankingData.put("totalReviews", ranking.getTotalReviews());
            rankingData.put("popularityScore", ranking.getPopularityScore());
            rankingData.put("tierRanking", ranking.getTierRanking());
            cloudResponse.add(rankingData);
        }
        saveToCloud(cloudResponse);

        List<RankingResponse> weeklyRankingResponse = new ArrayList<>();
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
