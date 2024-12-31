package com.bitescout.app.reviewservice.review;

import com.bitescout.app.reviewservice.review.dto.*;
import com.bitescout.app.reviewservice.review.exception.ReviewNotFoundException;
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
    private final ReviewInteractionRepository reviewInteractionRepository;
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

        var review = repository.findById(reviewId).orElseThrow(()->new ReviewNotFoundException("Review not found"));
        return mapper.toReviewResponse(review);
    }

    public ReviewResponse updateReview(ReviewUpdateRequest reviewRequest, String reviewId, String userId) {

        var existingReview = repository.findByIdAndCustomerId(reviewId, userId).orElseThrow(()->new ReviewNotFoundException("Review not found"));

        existingReview.setComment(reviewRequest.comment());
        existingReview.setRating(reviewRequest.rating());

        repository.save(existingReview);
        return mapper.toReviewResponse(existingReview);
    }

    public String getReviewOwner(String reviewId){
        Review review = repository.findById(reviewId).orElseThrow(() -> new BadRequestException("Review not found"));
        return review.getCustomerId();
    }

    public String getReviewInteractionOwer(String reviewInteractionId){
        return reviewInteractionRepository.findById(reviewInteractionId).get().getInteractingUserId();
    }


    public void deleteReview(String reviewId, String customerId) {
        var review = repository.findByIdAndCustomerId(reviewId, customerId).orElseThrow(() ->
                new BadRequestException("Review not found"));
        repository.deleteById(reviewId);
    }

    public void deleteReviewInteraction(String reviewInteractionId, String customerId) {
        var reviewInteraction = reviewInteractionRepository.findByIdAndInteractingUserId(reviewInteractionId, customerId).orElseThrow(() ->
                new BadRequestException("Review interaction not found"));
        reviewInteractionRepository.deleteById(reviewInteractionId);//interaction ı reviewInteraction repo dan silmeli
    }

    public int getLikeCountOfReview(String reviewId) {
        int likes = reviewInteractionRepository.
                findByReviewIdAndInteractionType(reviewId, InteractionType.LIKE).size();
        int dislikes = reviewInteractionRepository
                .findByReviewIdAndInteractionType(reviewId, InteractionType.DISLIKE).size();
        return likes - dislikes;
    }

    public ReviewInteractionResponseDTO getReviewInteractions(String reviewId) {
        List<ReviewInteraction> replies = reviewInteractionRepository.findByReviewIdAndInteractionType(reviewId, InteractionType.REPLY);
        ReviewInteractionResponseDTO response = new ReviewInteractionResponseDTO(
                getLikeCountOfReview(reviewId),
                replies
        );
        return response;
    }

    public ReviewInteraction createReviewInteraction(ReviewInteractionRequest request, String userId) {
        if(repository.findById(request.reviewId()).isEmpty()){
            throw new ReviewNotFoundException("Review not found");
        }
        if(request.interactionType() == InteractionType.LIKE){
            var likeInteraction = reviewInteractionRepository.findByReviewIdAndInteractingUserIdAndInteractionType(request.reviewId(), userId, InteractionType.LIKE);

            if(likeInteraction.isPresent()){
                throw new ReviewNotFoundException("Already liked this review");
            }
            var dislikeInteraction = reviewInteractionRepository.findByReviewIdAndInteractingUserIdAndInteractionType(request.reviewId(), userId, InteractionType.DISLIKE);
            dislikeInteraction.ifPresent(reviewInteractionRepository::delete);
        }
        else if(request.interactionType() == InteractionType.DISLIKE){
            var dislikeInteraction = reviewInteractionRepository.findByReviewIdAndInteractingUserIdAndInteractionType(request.reviewId(), userId, InteractionType.DISLIKE);

            if (dislikeInteraction.isPresent()) {
                throw new ReviewNotFoundException("Already disliked this review");
            }
            var likeInteraction = reviewInteractionRepository.findByReviewIdAndInteractingUserIdAndInteractionType(request.reviewId(), userId, InteractionType.LIKE);
            likeInteraction.ifPresent(reviewInteractionRepository::delete);
        }

        var interaction = ReviewInteraction.builder()
                .interactionType(request.interactionType())
                .reviewId(request.reviewId())

                .interactingUserId(userId)
                .replyText(request.replyText())
                .build();
        interactionRepository.save(interaction);
        return interaction;
    }
}
