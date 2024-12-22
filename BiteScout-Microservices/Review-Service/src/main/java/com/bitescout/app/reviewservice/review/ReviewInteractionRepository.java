package com.bitescout.app.reviewservice.review;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewInteractionRepository extends MongoRepository<ReviewInteraction, Long> {

}
