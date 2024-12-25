package com.bitescout.app.reviewservice.review;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
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
