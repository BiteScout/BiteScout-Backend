package com.bitescout.app.reviewservice.review;

import com.bitescout.app.reviewservice.review.dto.*;
import jakarta.validation.Valid;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.RecursiveTask;


@RestController
@RequestMapping("v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @RequestBody @Valid ReviewRequest reviewRequest,
            @RequestAttribute("userId") String userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(reviewRequest, userId));
    }

    @PostMapping("/interaction")
    public ResponseEntity<ReviewInteraction> createReviewInteraction(
            @RequestBody @Valid ReviewInteractionRequest request,
            @RequestAttribute("userId") String userId
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

    @PutMapping("/{reviewId}") // userId ReviewRequest'ten de alabiliriz headerdan da alabiliriz
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwner(#userId, principal)")
    public ResponseEntity<ReviewResponse> updateReview(
            @RequestBody @Valid ReviewUpdateRequest reviewRequest,
            @RequestAttribute("userId") String userId,
            @PathVariable("reviewId") String reviewId

    ){
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.updateReview(reviewRequest, reviewId, userId));

    }

    @GetMapping("/interaction/{reviewId}")
    public ResponseEntity<ReviewInteractionResponseDTO> getReviewInteractionsOfReview(
            @PathVariable("reviewId") String reviewId
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(reviewService.getReviewInteractions(reviewId));
    }

    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwnerDelete(#reviewId, principal)")
    public void deleteReview(
            @PathVariable("reviewId") String reviewId,
            @RequestAttribute("userId") String userId
    ){
        reviewService.deleteReview(reviewId, userId);
    }

    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping("/interaction/{reviewInteractionId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwnerDeleteInteraction(#reviewInteractionId, principal)")
    public void deleteReviewInteraction(
            @PathVariable("reviewInteractionId") String reviewInteractionId,
            @RequestAttribute("userId") String userId
    ){
        reviewService.deleteReviewInteraction(reviewInteractionId, userId);
    }


}
