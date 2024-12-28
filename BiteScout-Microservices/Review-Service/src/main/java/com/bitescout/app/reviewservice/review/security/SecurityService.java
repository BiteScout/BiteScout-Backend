package com.bitescout.app.reviewservice.review.security;


import com.bitescout.app.reviewservice.review.ReviewService;
import com.bitescout.app.reviewservice.review.client.UserServiceClient;
import com.bitescout.app.reviewservice.review.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final UserServiceClient userServiceClient;
    private final ReviewService reviewService;

    public boolean isOwner(String ownerId, String principal) {
        UserDTO user = userServiceClient.getUser(ownerId).getBody();
        if (user != null) {
            System.out.println("Owner ID: " + ownerId);
            System.out.println("Principal: " + principal);
            System.out.println("Owner Username: " + user.getUsername());
            return user.getUsername().equals(principal);
        }
        return false;
    }

    public boolean isOwnerDelete(String reviewId, String principal) {
        String ownerId = reviewService.getReviewOwner(reviewId);
        UserDTO user = userServiceClient.getUser(ownerId).getBody();
        if (user != null) {
            System.out.println("Owner ID: " + ownerId);
            System.out.println("Principal: " + principal);
            System.out.println("Owner Username: " + user.getUsername());
            return user.getUsername().equals(principal);
        }
        return false;
    }

    public boolean isOwnerDeleteInteraction(String reviewInteractionId, String principal) {
        String ownerId = reviewService.getReviewInteractionOwer(reviewInteractionId);
        UserDTO user = userServiceClient.getUser(ownerId).getBody();
        if (user != null) {
            System.out.println("Owner ID: " + ownerId);
            System.out.println("Principal: " + principal);
            System.out.println("Owner Username: " + user.getUsername());
            return user.getUsername().equals(principal);
        }
        return false;
    }





}