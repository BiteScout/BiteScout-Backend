package com.bitescout.app.reviewservice.review;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Document(collection = "review")
@Data
@AllArgsConstructor
@NoArgsConstructor
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
