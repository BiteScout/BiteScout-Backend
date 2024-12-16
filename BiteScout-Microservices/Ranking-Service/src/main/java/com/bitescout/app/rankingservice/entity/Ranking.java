package com.bitescout.app.rankingservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ratings")
public class Ranking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(nullable = false)
    private UUID restaurantId;

    @Column
    private double averageRating;

    @Column
    private int totalReviews;

    @Column
    private double popularityScore;

    @Enumerated(EnumType.STRING)
    private TierRanking tierRanking;

    @UpdateTimestamp
    private LocalDateTime updateTimestamp;




}
