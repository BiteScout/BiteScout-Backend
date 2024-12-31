package com.bitescout.app.reviewservice.review;

import com.bitescout.app.reviewservice.review.dto.ReviewRequest;
import com.bitescout.app.reviewservice.review.dto.ReviewResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReviewMapperTest {
    private ReviewMapper reviewMapper;

    @BeforeEach
    void setUp() {
        reviewMapper = new ReviewMapper();
    }

    @Test
    public void toReviewResponseTest() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime modified = LocalDateTime.now();
        Review review = new Review("123456", "654321", "123456",
                5, "nice", created, modified);

        ReviewResponse reviewResponse = reviewMapper.toReviewResponse(review);

        assertNotNull(reviewResponse);
        assertEquals(review.getId(), reviewResponse.id());
        assertEquals(review.getRestaurantId(), reviewResponse.restaurantId());
        assertEquals(review.getCustomerId(), reviewResponse.customerId());
        assertEquals(review.getRating(), reviewResponse.rating());
        assertEquals(review.getComment(), reviewResponse.comment());
        assertEquals(review.getCreatedAt(), reviewResponse.createdAt());
        assertEquals(review.getUpdatedAt(), reviewResponse.updatedAt());
    }

    @Test
    public void toReviewTest() {
        ReviewRequest reviewRequest = new ReviewRequest("123456", 5, "super");
        Review review = reviewMapper.toReview(reviewRequest, "user1");

        assertNotNull(review);
        assertEquals(review.getRestaurantId(), reviewRequest.restaurantId());
        assertEquals(review.getRating(), reviewRequest.rating());
        assertEquals(review.getComment(), reviewRequest.comment());

    }

    @Test
    public void reviewRequestNullTest() {
        var exp=assertThrows(NullPointerException.class, () -> reviewMapper.toReview(null, "user1"));
        assertEquals(exp.getMessage(), "reviewRequest is null");
    }

}