package com.bitescout.app.rankingservice.controller;

import com.bitescout.app.rankingservice.entity.Ranking;
import com.bitescout.app.rankingservice.entity.RankingResponse;
import com.bitescout.app.rankingservice.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;
    // Controller to get ratings by restaurant from review service and calculate ranking
    @PostMapping("/restaurant")
    public ResponseEntity<Ranking> submitRating(@RequestParam String restaurantId) {
        return ResponseEntity.ok(rankingService.submitRestaurantRating(restaurantId));
    }
    // Controller to post notifications to cloud run
    @GetMapping("/ranking")
    public ResponseEntity<List<RankingResponse>> notificationWeekly() {
        return ResponseEntity.ok(rankingService.getRanking());
    }

    // RankingResponse object with average rating, total reviews and popularity score
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<RankingResponse> getRatingsByRestaurant(@PathVariable String restaurantId) {
        return ResponseEntity.ok(rankingService.getRestaurantRating(restaurantId));
    }
    // Json object with average rating and total reviews
    @GetMapping("/restaurant/{restaurantId}/average")
    public ResponseEntity<Map<String, Object>> getAverageRating(@PathVariable String restaurantId) {
        double averageRating = rankingService.calculateAverageRating(restaurantId);
        int totalReviews = rankingService.getRestaurantTotalReviews(restaurantId);
        Map<String, Object> response = new HashMap<>();
        response.put("averageRating", averageRating);
        response.put("totalReviews", totalReviews);
        return ResponseEntity.ok(response);
    }
}