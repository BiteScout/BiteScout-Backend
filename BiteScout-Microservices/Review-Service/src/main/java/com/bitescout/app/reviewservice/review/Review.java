package com.bitescout.app.reviewservice.review;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Document(collection = "review")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    @Id
    private String id;
    private String restaurantId;
    private String customerId;
    private int rating;
    private String comment;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

}
