
# User Service Microservice Documentation

## Overview
The **User Service Microservice** is part of BiteScout application that manages user-related operations, including registration, user profiles, updating user information, and managing user favorites.

---

## Features
- **User Management**: Create, read, update, delete, and enable user accounts.
- **Authentication**: Uses JWT-based authentication for secure access.
- **Favorites Management**: Add, fetch, and remove user favorites for restaurants.
- **File Upload**: Handles user profile picture uploads and deletions.

---

## Architecture
- **Spring Boot**: Core framework for developing the microservice.
- **Spring Security**: Configures security, including JWT authentication.
- **ModelMapper**: For object mapping between entities and DTOs.
- **Feign Client**: For communication with other microservices.
- **Hibernate**: For ORM and database interaction.

---

## Package Structure
```
com.bitescout.app.userservice
├── configuration      # Configuration classes for beans and security
├── controller         # RESTful controllers for exposing APIs
├── dto                # Data Transfer Objects for external communication
├── entity             # Entity classes mapped to database tables
├── exc                # Handles exceptions
├── jwt                # Handles jwt authentication features
├── repository         # To handle queries from database
└── service            # Business logic and service layer
```

---

## Key Components

### 1. **Configuration**

#### **BeanConfig**
- Configures `ModelMapper` for object mapping.
- Configures `BCryptPasswordEncoder` for password encryption.
- Configures `RestTemplate`.
#### **SecurityConfig**
- Configures JWT-based security.
---

### 2. **Controller**

#### **UserController**
Handles user and favorite-related endpoints.

##### User Endpoints:
- `POST /save`: Creates a new user.
- `GET /{userId}`: Fetches user details.
- `PUT /update`: Updates user details and profile picture.
- `DELETE /{userId}`: Deletes a user.
- `GET /getAll`: Retrieves all users (Admin-only).
- `GET /getUserByUsername/{username}`: Retrieves user by their username.
- `PUT /enable-user/{userId}`: Enables user as active.
- `DELETE /username/{username}`: Deletes user by their username.

##### Favorites Endpoints:
- `POST /{userId}/favorites/{restaurantId}`: Adds a favorite restaurant for a user.
- `GET /{userId}/favorites`: Retrieves all favorites of a user.
- `DELETE /{userId}/favorites/{restaurantId}`: Removes a favorite restaurant for a user.
- `GET /favoriteCount/{restaurantId}`: Counts the number of users favoriting a restaurant.

---

### 3. **Entities**

#### **User**
Represents a user in the system.
- **Fields**: `id`, `username`, `password`, `email`, `enabled`, `role`, `userDetails`, `creationTimestamp`, `updateTimestamp`.

#### **Favorite**
Represents a user's favorite restaurant.
- **Fields**: `id`, `user`, `restaurantId`, `favoritedAt`.

#### **UserDetails**
Embeddable object holding additional user information.
- **Fields**: `firstName`, `lastName`, `phoneNumber`, `country`, `city`, `postalCode`, `address`, `profilePicture`.

#### **Role**
Enumeration defining user roles: `ADMIN`, `CUSTOMER`, `RESTAURANT_OWNER`.

---

### 4. **DTOs**
- **RegisterRequestDTO**: Captures data for user registration.
- **UserDTO**: Represents a user for API responses.
- **UserAuthDTO**: Represents user authentication data response.
- **UserUpdateRequestDTO**: Captures data for updating user information.
- **FavoriteResponseDTO**: Represents a favorite restaurant for API responses.


## JSON Examples

### Register Request
```json
{
  "username": "johndoe123",
  "password": "securePass123",
  "email": "johndoe@example.com",
  "role": "CUSTOMER"
}
```

### User Update Request
```json
{
  "id": "2e5144e0-9ff6-46df-b254-bb98c8a2a8a6",
  "username": "newUsername",
  "password": "newSecurePass123",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "1234567890",
  "country": "USA",
  "city": "New York",
  "postalCode": "10001",
  "address": "123 Main Street",
  "profilePicture": "profilePic.jpg"
}
```

### UserDTO Response
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "username": "johndoe",
  "email": "johndoe@example.com",
  "enabled": true,
  "userDetails": {
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+1234567890",
    "country": "USA",
    "city": "New York",
    "postalCode": "10001",
    "address": "123 Main Street, Apartment 4B",
    "profilePicture": "johndoe.jpg"
  }
}

```

### UserAuthDTO Response
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "username": "john_doe",
  "email": "john.doe@example.com",
  "password": "password123",
  "role": "USER",
  "enabled": true
}

```

### Favorite Response
```json
{
  "id": "456e4567-e89b-12d3-a456-426614174001",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "restaurantId": "789e4567-e89b-12d3-a456-426614174002",
  "favoritedAt": "2024-12-10T10:00:00"
}
```
---

## Security
- **Authentication**: Implements JWT for stateless authentication.
- **Authorization**: Uses role-based and ownership-based permissions for endpoints.
- **Password Encryption**: Uses `BCryptPasswordEncoder` to secure user passwords.

---

## Database Schema

### User Table
| Column           | Type              | Constraints                |
|-------------------|-------------------|----------------------------|
| id               | UUID              | Primary Key                |
| username         | String            | Unique, Not Null           |
| password         | String            | Not Null                   |
| email            | String            | Unique, Not Null           |
| enabled          | Boolean           | Not Null                   |
| role             | Enum (Role)       | Not Null                   |
| userDetails      | Embedded          | -                          |
| creationTimestamp| LocalDateTime     | Auto-generated             |
| updateTimestamp  | LocalDateTime     | Auto-generated             |

### Favorites Table
| Column           | Type              | Constraints                |
|-------------------|-------------------|----------------------------|
| id               | UUID              | Primary Key                |
| user_id          | UUID              | Foreign Key (User)         |
| restaurant_id    | UUID              | Not Null                   |
| favorited_at     | LocalDateTime     | Auto-generated             |

---

