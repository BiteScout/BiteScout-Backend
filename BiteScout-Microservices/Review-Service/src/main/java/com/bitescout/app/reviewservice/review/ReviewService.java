package com.bitescout.app.reviewservice.review;

import com.bitescout.app.reviewservice.review.dto.ReviewInteractionRequest;
import com.bitescout.app.reviewservice.review.dto.ReviewRequest;
import com.bitescout.app.reviewservice.review.dto.ReviewResponse;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewInteractionRepository interactionRepository;
    private final ReviewRepository repository;
    private final ReviewMapper mapper;

    public ReviewResponse createReview(ReviewRequest reviewRequest, String userId) {
        var review = mapper.toReview(reviewRequest, userId);
        repository.save(review);
        return mapper.toReviewResponse(review);
    }

    public List<ReviewResponse> getReviews(String restaurantId) {
        return repository.findByRestaurantId(restaurantId)
                .stream()
                .map(mapper::toReviewResponse)
                .filter(review -> review.createdAt() != null) // Filter out reviews with null createdAt
                .sorted(Comparator.comparing(ReviewResponse::createdAt).reversed())
                .collect(Collectors.toList());
    }

    public ReviewResponse getReview(String reviewId) {
        var review = repository.findById(reviewId).get();
        return mapper.toReviewResponse(review);
    }

    public ReviewResponse updateReview(ReviewRequest reviewRequest, String reviewId){
        var existingReview = repository.findById(reviewId).get();
        existingReview.setComment(reviewRequest.comment());
        existingReview.setRating(reviewRequest.rating());

        repository.save(existingReview);
        return mapper.toReviewResponse(existingReview);
    }

    public String getReviewOwner(String reviewId){
        Review review = repository.findById(reviewId).orElseThrow(() -> new BadRequestException("review not found"));
        return review.getCustomerId();
    }

    public void deleteReview(String reviewId){
        repository.deleteById(reviewId);
    }

    public ReviewInteraction createReviewInteraction(ReviewInteractionRequest request) {
        if(repository.findById(request.reviewId()).isEmpty()){
            throw new BadRequestException("review not found");
        }
        var interaction = ReviewInteraction.builder()
                .interactionType(request.interactionType())
                .reviewId(request.reviewId())
                .userId((request.userId()))
                .replyText(request.replyText())
                .build();
        interactionRepository.save(interaction);
        return interaction;
    }
}
