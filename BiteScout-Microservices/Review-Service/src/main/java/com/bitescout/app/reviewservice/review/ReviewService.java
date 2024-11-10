package com.bitescout.app.reviewservice.review;

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


    public List<ReviewResponse> getReviews(Long restaurantId){
        return repository.findByRestaurantId(restaurantId)
                .stream()
                .map(mapper::toReviewResponse)
                .sorted(Comparator.comparing(ReviewResponse::createdAt).reversed())
                .collect(Collectors.toList());
    }
}
