package com.bitescout.app.reviewservice.review;

import com.bitescout.app.reviewservice.review.dto.*;
import com.bitescout.app.reviewservice.review.exception.ReviewMissingFieldException;
import com.bitescout.app.reviewservice.review.exception.ReviewNotFoundException;
import jakarta.ws.rs.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ReviewServiceTest {
    //Unit tests

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewInteractionRepository reviewInteractionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createReviewTest() {
        ReviewRequest reviewRequest = new ReviewRequest("r1", 3, "good");
        Review review = new Review("rev1", "r1", "c1", 3, "good", LocalDateTime.now(), LocalDateTime.now());

        Mockito.when(reviewMapper.toReview(reviewRequest,"c1")).thenReturn(review);
        Mockito.when(reviewRepository.save(review)).thenReturn(review);
        Mockito.when(reviewMapper.toReviewResponse(review))
                .thenReturn(new ReviewResponse("rev1", "r1",
                        "c1", 3, "good",
                        LocalDateTime.now(), LocalDateTime.now()));


        ReviewResponse reviewResponse = reviewService.createReview(reviewRequest, "c1");

        assertNotNull(reviewResponse);
        assertEquals(reviewResponse.comment(),reviewRequest.comment());
        assertEquals(reviewResponse.restaurantId(),reviewRequest.restaurantId());
        assertEquals(reviewResponse.rating(),reviewRequest.rating());
    }

    @Test
    public void createReviewWithMissingFields() {
        ReviewRequest reviewRequest = new ReviewRequest("r1", 3, "good");

        var exp=assertThrows(ReviewMissingFieldException.class,
                ()->reviewService.createReview(reviewRequest,""));
        assertEquals(exp.getMessage(),"User ID and/or Restaurant ID cannot be empty");

    }

    @Test
    public void getReviewsTest() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();
        Review review = new Review("rev1", "r1", "c1", 3, "good", created, updated);
        ReviewResponse expectedResponse = new ReviewResponse("rev1", "r1","c1", 3,"good", created, updated);

        Mockito.when(reviewRepository.findByRestaurantId(review.getRestaurantId())).thenReturn(List.of(review));
        Mockito.when(reviewMapper.toReviewResponse(review))
                .thenReturn(new ReviewResponse("rev1", "r1",
                        "c1", 3, "good",
                        created, updated));

        List<ReviewResponse> reviewResponses = reviewService.getReviews(review.getRestaurantId());
        assertNotNull(reviewResponses);
        assertEquals(1,reviewResponses.size());
        assertEquals(expectedResponse,reviewResponses.get(0));
    }

    @Test
    public void getReviewTest(){
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();

        Review review = new Review("rev1", "r1", "c1", 3, "good", created, updated);
        ReviewResponse expectedResponse = new ReviewResponse("rev1", "r1", "c1", 3, "good", created, updated);

        Mockito.when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
        Mockito.when(reviewMapper.toReviewResponse(review)).thenReturn(expectedResponse);

        ReviewResponse response = reviewService.getReview(review.getId());

        assertEquals(expectedResponse, response);
    }

    @Test
    public void getNonExistentReviewTest(){
        Mockito.when(reviewRepository.findById("invalidId")).thenReturn(Optional.empty());
        var exp=assertThrows(ReviewNotFoundException.class, ()->reviewService.getReview("invalidId"));
        assertEquals(exp.getMessage(),"Review not found");
    }

    @Test
    public void updateReviewTest() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();
        ReviewUpdateRequest updateRequest = new ReviewUpdateRequest(4,"Updated comment" );
        Review review = new Review("rev1", "r1", "c1", 3, "good", created, updated);


        Mockito.when(reviewRepository.findByIdAndCustomerId(review.getId(), review.getCustomerId())).thenReturn(Optional.of(review));
        Mockito.when(reviewMapper.toReviewResponse(review)).thenReturn(new ReviewResponse(review.getId(), review.getRestaurantId(), review.getCustomerId(), updateRequest.rating(), updateRequest.comment(), created,LocalDateTime.now()));

        ReviewResponse response = reviewService.updateReview(updateRequest, review.getId(), review.getCustomerId());

        Mockito.verify(reviewRepository).save(review);
        assertEquals("Updated comment", review.getComment());
        assertEquals(4, review.getRating());
        assertNotNull(response);
    }

    @Test
    public void updateNonExistentReviewTest(){
        String reviewId = "nonExistentId";
        String userId = "userId";
        ReviewUpdateRequest updateRequest = new ReviewUpdateRequest(4, "Updated comment");

        Mockito.when(reviewRepository.findByIdAndCustomerId(reviewId, userId)).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class, () -> reviewService.updateReview(updateRequest, reviewId, userId));
    }

    @Test
    public void deleteReviewTest() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();
        Review review = new Review("rev1", "r1", "c1", 3, "good", created, updated);


        Mockito.when(reviewRepository.findByIdAndCustomerId(review.getId(), review.getCustomerId())).thenReturn(Optional.of(review));

        reviewService.deleteReview(review.getId(), review.getCustomerId());

        Mockito.verify(reviewRepository).deleteById(review.getId());
    }

    @Test
    public void createReviewInteractionTest(){
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();
        String reviewId = "reviewId";
        String userId = "userId";
        Review review = new Review(reviewId, "r1", userId, 3, "good", created, updated);

        ReviewInteractionRequest request = new ReviewInteractionRequest(reviewId, InteractionType.LIKE, null);

        Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        Mockito.when(reviewInteractionRepository.findByReviewIdAndInteractingUserIdAndInteractionType(reviewId, userId, InteractionType.LIKE))
                .thenReturn(Optional.empty());

        reviewService.createReviewInteraction(request, userId);

        Mockito.verify(reviewInteractionRepository).save(Mockito.any(ReviewInteraction.class));

    }

    @Test
    public void createInteractionForNonExistentReview(){
        String reviewId = "nonExistentId";
        String userId = "userId";
        ReviewInteractionRequest request = new ReviewInteractionRequest(reviewId, InteractionType.LIKE, null);

        Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        var exp=assertThrows(ReviewNotFoundException.class, () -> reviewService.createReviewInteraction(request, userId));
        assertEquals(exp.getMessage(),"Review not found");
    }

    @Test
    void testGetLikeCountOfReview() {
        String reviewId = "reviewId";

        Mockito.when(reviewInteractionRepository.findByReviewIdAndInteractionType(reviewId, InteractionType.LIKE)).thenReturn(List.of(new ReviewInteraction()));
        Mockito.when(reviewInteractionRepository.findByReviewIdAndInteractionType(reviewId, InteractionType.DISLIKE)).thenReturn(List.of());

        int likeCount = reviewService.getLikeCountOfReview(reviewId);

        assertEquals(1, likeCount);
    }

    @Test
    public void getReviewInteractionsTest(){
        String reviewId = "reviewId";
        String userId = "userId";
        String replyText = "replyText";
        ReviewInteraction replies=ReviewInteraction.builder()
                .interactionType(InteractionType.REPLY)
                .reviewId(reviewId)

                .interactingUserId(userId)
                .replyText(replyText)
                .build();
        Mockito.when(reviewInteractionRepository.
                findByReviewIdAndInteractionType(reviewId, InteractionType.REPLY)).
                thenReturn(List.of(replies));

        //Mockito.when(reviewService.getLikeCountOfReview(reviewId)).thenReturn(1);
        ReviewInteractionResponseDTO expectedResponse = new ReviewInteractionResponseDTO(
                0,
                List.of(replies)
        );

        ReviewInteractionResponseDTO actualDTO= reviewService.getReviewInteractions(reviewId);
        assertEquals(expectedResponse,actualDTO);
    }

    @Test
    public void testDeleteReviewInteraction_ShouldDeleteInteractionWhenFound() {
        String interactionId = "interactionId";
        String customerId = "userId";
        String reviewId = "reviewId";
        String userId = "userId";
        String replyText = "replyText";
        ReviewInteraction reviewInteraction = ReviewInteraction.builder()
                .interactionType(InteractionType.REPLY)
                .reviewId(reviewId)

                .interactingUserId(userId)
                .replyText(replyText)
                .build();

        Mockito.when(reviewInteractionRepository.findByIdAndInteractingUserId(interactionId, customerId)).thenReturn(Optional.of(reviewInteraction));

        reviewService.deleteReviewInteraction(interactionId, customerId);

        Mockito.verify(reviewInteractionRepository).deleteById(interactionId);
    }

    @Test
    public void testGetReviewOwner_ShouldReturnOwnerId() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();
        String reviewId = "reviewId";
        String expectedOwnerId = "ownerId";
        Review review = new Review(reviewId, "r1", expectedOwnerId, 3,
                "good", created, updated);


        Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        String ownerId = reviewService.getReviewOwner(reviewId);

        assertEquals(expectedOwnerId, ownerId);
    }




}