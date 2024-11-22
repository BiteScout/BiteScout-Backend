package com.bitescout.app.reviewservice.review;

import com.bitescout.app.reviewservice.review.dto.ReviewRequest;
import com.bitescout.app.reviewservice.review.dto.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository repository;
    private final ReviewMapper mapper;

    public ReviewResponse createReview(ReviewRequest reviewRequest, Long userId) {
        var review = mapper.toReview(reviewRequest, userId);
        repository.save(review);
        return mapper.toReviewResponse(review);
    }

    public List<ReviewResponse> getReviews(Long restaurantId) {
        return repository.findByRestaurantId(restaurantId)
                .stream()
                .map(mapper::toReviewResponse)
                .sorted(Comparator.comparing(ReviewResponse::createdAt).reversed())
                .collect(Collectors.toList());
    }

    public ReviewResponse getReview(Long reviewId) {
        var review = repository.findById(reviewId).get();
        return mapper.toReviewResponse(review);
    }

    public ReviewResponse updateReview(ReviewRequest reviewRequest, Long reviewId){
        var existingReview = repository.findById(reviewId).get();
        existingReview.setComment(reviewRequest.comment());
        existingReview.setRating(reviewRequest.rating());

        repository.save(existingReview);
        return mapper.toReviewResponse(existingReview);
    }

    public void deleteReview(Long reviewId){
        repository.deleteById(reviewId);
    }
}
