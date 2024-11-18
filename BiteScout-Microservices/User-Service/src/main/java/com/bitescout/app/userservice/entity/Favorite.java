package com.bitescout.app.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "favorites")
public class Favorite {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;

    @Column(name = "favorited_at")
    private LocalDateTime favoritedAt;

    @PrePersist
    public void onFavorite() {
        this.favoritedAt = LocalDateTime.now();
    }
}
