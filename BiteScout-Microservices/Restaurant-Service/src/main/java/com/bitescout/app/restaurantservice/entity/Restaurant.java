package com.bitescout.app.restaurantservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "restaurants")
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID ownerId;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    private String menu;

    private String cuisineType;

    @Column(columnDefinition = "geography(Point,4326)", nullable = false)
    private Point location;

    private String priceRange;

    @Column (nullable = false)
    private LocalDateTime createdAt;

    @Column (nullable = false)
    private LocalDateTime updatedAt;

    // One-to-Many relationship with SpecialOffer
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpecialOffer> specialOffers = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Images> images;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
