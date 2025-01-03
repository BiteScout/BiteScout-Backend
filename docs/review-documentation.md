
# Review Service API Documentation

## Overview

The **Review Service** is a microservice of BiteScout enabling users to make reviews about restaurants. It includes CRUD operations
for reviews and also interaction with them by likes/dislikes and replies. 

### Key Features:
- **Review Management**: Add, read, update and delete reviews about restaurants.
- **Review Interaction Management**: Add, read and delete interactions for reviews.

---

## Table of Contents
1. [Controller](#controller)
2. [Entities](#entities)
3. [DTOs](#dtos)
4. [Repositories](#repositories)


---

## Controller

### ReviewController
Provides endpoints for reviews and review interactions.

**Base URL**: `/v1/reviews/`

| Method | Endpoint                                   | Description                        |
|--------|--------------------------------------------|------------------------------------|
| POST   |`/`                                         | Create a new review                |
| POST   | `/interaction`                             | Create an interaction for a review |
| GET    | `/restaurants/{restaurant-id}`             | Get all reviews for a restaurant   |
| GET    | `{reviewId}`                               | View details for a review          |
| PUT    | `{reviewId}`                               | Update a review                    |
| GET    | `/interaction/{reviewId}`                  | See interactions for a review      |
| DELETE | `/{reviewId}`                              | Delete a review                    |
| DELETE | `/interaction/{reviewInteractionId}`       | Delete a review interaction        |


---

## Entities

### Review
Represents the `Review` entity that is stored in `review` collection.

```java
@Document(collection = "review")
public class Review {

    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String restaurantId;
    private String customerId;
    private Integer rating;
    private String comment;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

}

```

### ReviewInteraction
Represents the `ReviewInteraction` entity that is stored in `reviewInteraction` collection.

```java
@Document(collection = "reviewInteraction")
public class ReviewInteraction {
    @Id
    private String id;
    private String reviewId;

    private String interactingUserId;
    private InteractionType interactionType;
    private String replyText;
    @CreatedDate
    private LocalDateTime createdAt;
}
```

---

## DTOs

### ReviewRequest DTO
Used to create review.

**Example JSON**:
````json
{
    "restaurantId":"f064f251-8a39-469a-8db7-fc94e9eea3f1",
    "rating":4,
    "comment":"review comment"
}
````

### ReviewUpdateRequest DTO
Used to update review.

**Example JSON**:
````json
{
    "rating":4,
    "comment":"review comment"
}
````

### ReviewResponse DTO
Used to return review data in response.

**Example JSON**:
````json
{
    "id":"f064f251-8a39-469a-8db7-fc94e9eea3f1",
    "restaurantId":"f8eb82e3-8a39-469a-8db7-fc94e9eea3f1",
    "customerId":"99447f77-f8e7-4699-a489-a36e814ed2c3",
    "rating":4,
    "comment":"review comment",
    "createdAt": "2024-12-01T12:00:00",
    "updatedAt": "2024-12-10T15:00:00"
}
````

### ReviewInteractionRequest DTO
Used to create review interaction.

**Example JSON**:
````json
{
  "reviewId":"676eaef376561b1fbe9f9089",
  "interactionType":"REPLY",
  "replyText":"review comment"
}
````

### ReviewInteractionResponse DTO
Used to return review interaction data in response.

**Example JSON**:
````json
{
  "likeCount":"5",
  "replies": [{
    "id": "446eaef376561b1fbe9f9089",
    "reviewId": "676eaef376561b1fbe9f9089",
    "interactingUserId": "636eaef376561b1fbe9f9089",
    "interactionType": "REPLY",
    "replyText": "agree",
    "createdAt": "2024-12-01T12:00:00"
  }]
  
}
````

### User DTO
Used to transfer user data.

**Example JSON**:
````json
{
  "id": "626eaef376561b1fbe9f9089",
  "username": "johndoe1773"
}

````
---

## Repositories

### ReviewResponse
Defines methods to access `Review` entities.

````java
public interface ReviewRepository extends MongoRepository<Review,String>/*JpaRepository<Review, Long>*/{


    public List<Review> findByRestaurantId(String restaurantID);
    public Optional<Review> findById(String id);
    public Optional<Review> findByIdAndCustomerId(String id, String customerId);

}
````

### ReviewInteractionRepository
Defines methods to access `ReviewInteraction` entities.

````java
public interface ReviewInteractionRepository extends MongoRepository<ReviewInteraction, String> {
    public Optional<ReviewInteraction> findByIdAndInteractingUserId(String id, String interactingUserId);
    public List<ReviewInteraction> findByReviewIdAndInteractionType(String reviewId, InteractionType interactionType);
    public Optional<ReviewInteraction> findByReviewIdAndInteractingUserIdAndInteractionType(String reviewId, String interactingUserId, InteractionType interactionType);
}
````

