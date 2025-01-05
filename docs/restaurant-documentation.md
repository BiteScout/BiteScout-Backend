
# Restaurant Service API Documentation

## Overview

The **Restaurant Service** is a mciroservice of BiteScout for managing restaurants and their menus profiles & special offers. This service includes CRUD operations for restaurants, functionality for searching restaurants by various parameters, and managing special offers for restaurants.

### Key Features:
- **Restaurant Management**: Add, update, delete, and retrieve restaurant information including menus.
- **Special Offers Management**: Add, update, delete, and retrieve special offers for restaurants.
- **Search and Filter**: Search for restaurants by name, cuisine type, price range, etc.

---

## Table of Contents
1. [Configuration](#configuration)
2. [Controllers](#controllers)
3. [Entities](#entities)
4. [DTOs](#dtos)
5. [Repositories](#repositories)
6. [Example JSON Requests/Responses](#example-json-requestsresponses)

---

## Configuration

### BeanConfig
Defines common beans used throughout the application like Modelmapper to map between classes.

```java
@Configuration
public class BeanConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.LOOSE)
            .setPropertyCondition(Conditions.isNotNull());
        return modelMapper;
    }
}
```
---

## Controllers

### RestaurantController
Provides endpoints for managing restaurants and special offers.

**Base URL**: `/v1/restaurants/`

| Method | Endpoint                          | Description                             |
|--------|-----------------------------------|-----------------------------------------|
| POST   | `/`                               | Create a new restaurant.               |
| GET    | `/`                               | Retrieve all restaurants.              |
| GET    | `/{restaurantId}`                 | Retrieve a specific restaurant.        |
| GET    | `/owner/{ownerId}`                      | Retrieve restaurants of specific owner.|
| PUT    | `/{restaurantId}`                 | Update a restaurant.                   |
| PUT    | `/{restaurantId}/update-menu`     | Update the menu of a restaurant        |
| DELETE | `/{restaurantId}`                 | Delete a restaurant.                   |
| GET    | `/near-me`                        | Find restaurants near a location.      |
| GET    | `/search`                         | Filter restaurants by their name.      |
| GET    | `/cuisine`                        | Filter restaurants by cuisine type.    |
| GET    | `/price`                          | Filter restaurants by price range.     |
| POST   | `/{restaurantId}/offers`          | Add a special offer.                   |
| GET    | `/{restaurantId}/offers`          | Get special offers for a restaurant.   |
| PUT    | `/{restaurantId}/offers/{offerId}`     | Update an existing special offer.   |
| DELETE    | `/{restaurantId}/offers/{offerId}`  | Remove a special offer from a restaurant.   |

---

## Entities

### Restaurant
Represents the `Restaurant` entity stored in the `restaurants` table.

```java
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

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpecialOffer> specialOffers = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Images> images;

    @Column (nullable = false)
    private LocalDateTime createdAt;

    @Column (nullable = false)
    private LocalDateTime updatedAt;
```

### SpecialOffer
Represents the `SpecialOffer` entity stored in the `special_offers` table.

```java
@Entity
@Table(name = "special_offers")
public class SpecialOffer {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false) private String title;
    @Column(nullable = false) private String description;
    @Column(nullable = false) private LocalDate startDate;
    @Column(nullable = false) private LocalDate endDate;

    @Column(nullable = false) private LocalDateTime createdAt;
    @Column(nullable = false) private LocalDateTime updatedAt;
}

### Images
Represents the `Images` entity stored in the `images` table.

```java
@Entity
@Table(name = "images")
public class Images{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;  // This links the image to a restaurant
}
```

---

## DTOs

### RestaurantRequestDTO
Used to create or update restaurant data.

**Example JSON**:
```json
{
  "ownerId": "bfc97d79-c787-47bc-a276-e3f735c7658d",
  "name": "Delicious XXX",
  "description": "A nice place offering a mix of global and local cuisines.",
  "menu": "https://example.com/qr-codes/menu1234526",
  "cuisineType": "Italian",
  "location": {
    "type": "Point",
    "coordinates": [12.4924, 41.8902]
  },
  "priceRange": "$$$"
}
```

### RestaurantResponseDTO
Used to return restaurant data in responses.

**Example JSON**:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174001",
  "ownerId": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Sushi Haven",
  "description": "A delightful sushi experience.",
  "menu": "Sushi Rolls, Sashimi, Miso Soup",
  "cuisineType": "Japanese",
  "location": "[12.4924, 41.8902]",
  "priceRange": "$$$",
  "createdAt": "2024-12-01T12:00:00",
  "updatedAt": "2024-12-10T15:00:00"
}
```

### SpecialOfferRequestDTO
Used to create or update special offers.

**Example JSON**:
```json
{
  "title": "Holiday Special",
  "description": "20% off all menu items!",
  "startDate": "2024-12-15",
  "endDate": "2025-01-01"
}
```

### SpecialOfferResponseDTO
Used to return special offer data in responses.

**Example JSON**:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174002",
  "title": "Holiday Special",
  "description": "20% off all menu items!",
  "startDate": "2024-12-15",
  "endDate": "2025-01-01",
  "createdAt": "2024-12-10T12:00:00",
  "updatedAt": "2024-12-10T15:00:00"
}
```

---

## Repositories

### RestaurantRepository
Defines methods for accessing `Restaurant` entities.

```java
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
    List<Restaurant> findByOwnerId(UUID ownerId);

    @Query(value = "SELECT * FROM restaurants r WHERE ST_DWithin(r.location, ST_MakePoint(:longitude, :latitude)::geography, :radius)", nativeQuery = true)
    List<Restaurant> findByLocationNear(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radiusInMeters
    );
    List<Restaurant> findByNameContainingIgnoreCase(String query);
    Restaurant findByName(String name);

    List<Restaurant> findByCuisineTypeContainingIgnoreCase(String cuisineType);
    List<Restaurant> findByPriceRangeIgnoreCase(String priceRange);
}
```

### SpecialOfferRepository
Defines methods for accessing `SpecialOffer` entities.

```java
@Repository
public interface SpecialOfferRepository extends JpaRepository<SpecialOffer, UUID> {
    List<SpecialOffer> findAllByRestaurantId(UUID restaurantId);

    SpecialOffer findByRestaurant_NameAndTitle(String restaurantName, String title);
}
```

### Images Repository
Defines methods for accessing `Images` entities.

```java
@Repository
public interface ImagesRepository extends JpaRepository<Images, UUID> {
    List<Images> findAllByRestaurant(Restaurant restaurant);
    void deleteAllByRestaurant(Restaurant restaurant);
}
}
```
