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
@RequestMapping("/v1/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;
    // Controller to get ratings by restaurant from review service and calculate ranking
    @PostMapping("/submit")
    public ResponseEntity<Void> submitRating() {
        rankingService.submitRestaurantRating();
        return ResponseEntity.ok().build();
    }
    // Controller to post notifications to cloud run
    @GetMapping("/weekly")
    public ResponseEntity<List<RankingResponse>> notificationWeekly() {
        return ResponseEntity.ok(rankingService.getRanking());
    }

    // RankingResponse object with average rating, total reviews and popularity score
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<RankingResponse> getRatingsByRestaurant(@PathVariable String restaurantId) {
        return ResponseEntity.ok(rankingService.getRestaurantRating(restaurantId));
    }
}