package com.bitescout.app.reviewservice.review;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends MongoRepository<Review,String>/*JpaRepository<Review, Long>*/{


    public List<Review> findByRestaurantId(String restaurantID);
    public Optional<Review> findById(String id);
    public Optional<Review> findByIdAndCustomerId(String id, String customerId);

}
