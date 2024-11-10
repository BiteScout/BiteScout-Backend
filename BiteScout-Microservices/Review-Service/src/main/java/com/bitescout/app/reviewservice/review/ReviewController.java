package com.bitescout.app.reviewservice.review;

import com.bitescout.app.reviewservice.review.dto.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    public ResponseEntity<List<ReviewResponse>> getReviews(@RequestHeader(value="Restaurant-Id") Long restaurantId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(reviewService.getReviews(restaurantId));
    }
}
