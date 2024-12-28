package com.bitescout.app.reviewservice.review;

import java.util.Optional;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewInteractionRepository extends MongoRepository<ReviewInteraction, String> {
    public Optional<ReviewInteraction> findByIdAndInteractingUserId(String id, String interactingUserId);
    public List<ReviewInteraction> findByReviewIdAndInteractionType(String reviewId, InteractionType interactionType);
    public Optional<ReviewInteraction> findByReviewIdAndInteractingUserIdAndInteractionType(String reviewId, String interactingUserId, InteractionType interactionType);
}
