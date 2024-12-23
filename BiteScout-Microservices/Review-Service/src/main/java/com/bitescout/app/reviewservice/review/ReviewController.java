package com.bitescout.app.reviewservice.review;

import com.bitescout.app.reviewservice.review.dto.ReviewInteractionRequest;
import com.bitescout.app.reviewservice.review.dto.ReviewRequest;
import com.bitescout.app.reviewservice.review.dto.ReviewResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @RequestBody @Valid ReviewRequest reviewRequest,
            @RequestHeader("User-Id") String userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(reviewRequest, userId));
    }

    @PostMapping("/interaction")
    public ResponseEntity<ReviewInteraction> createReviewInteraction(
            @RequestBody @Valid ReviewInteractionRequest request,
            @RequestHeader("User-Id") String userId
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(reviewService.createReviewInteraction(request, userId));
    }

    //Get all reviews
    @GetMapping("/restaurants/{restaurant-id}")
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable(value = "restaurant-id") String restaurantId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(reviewService.getReviews(restaurantId));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReview(
            @PathVariable("reviewId") String reviewId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(reviewService.getReview(reviewId));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @RequestBody ReviewRequest reviewRequest,
            @PathVariable("reviewId") String reviewId
    ){
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.updateReview(reviewRequest,reviewId));

    }

    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping("/{reviewId}")
    public void deleteReview(
            @PathVariable("reviewId") String reviewId
    ){
        reviewService.deleteReview(reviewId);
    }


}
