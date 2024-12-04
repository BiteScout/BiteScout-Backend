package com.bitescout.app.notificationservice.review;

import com.bitescout.app.notificationservice.restaurant.RestaurantResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(
        name = "review-service",
        url ="${application.config.reviews-url}"
)
public interface ReviewClient {

    @GetMapping("/{review-id}")
    Optional<ReviewResponse> getReview(@PathVariable("review-id") Long reviewId);

}
