package com.bitescout.app.reviewservice.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review,String>/*JpaRepository<Review, Long>*/{

    public List<Review> findByRestaurantId(String restaurantID);
}
