package com.bitescout.app.rankingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "review-service",
        url ="${application.config.reviews-url}"
)@Component
public interface ReviewClient {

    @GetMapping("/restaurant/{restaurantId}")
    ResponseEntity<List<ReviewDto>> getReviewsByRestaurant(@PathVariable("restaurantId") UUID restaurantId);

}