package com.bitescout.app.reviewservice.review;

import com.bitescout.app.reviewservice.review.dto.ReviewRequest;
import com.bitescout.app.reviewservice.review.dto.ReviewResponse;
import org.springframework.stereotype.Service;

@Service
public class ReviewMapper {
    public ReviewResponse toReviewResponse(Review review) {
        return new ReviewResponse(review.getId(), review.getRestaurantId(),
                review.getCustomerId(), review.getRating(), review.getComment(),
                review.getCreatedAt(), review.getUpdatedAt());
    }

    public Review toReview(ReviewRequest reviewRequest, Long userId){
        return Review.builder().customerId(userId)
                .restaurantId(reviewRequest.restaurantId())
                .rating(reviewRequest.rating())
                .comment(reviewRequest.comment())
                .build();
    }
}
