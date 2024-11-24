# Notifications

The Notifications module handles the creation, retrieval, update, and deletion of notifications. It consists of two main components:

1. **NotificationsConsumer Service** - Consumes Kafka topics and generates notifications.
2. **NotificationController** - Provides RESTful APIs for managing notifications.

---

## NotificationsConsumer Service

### Overview

The `NotificationsConsumer` service listens to Kafka topics and generates notifications based on the messages received. These notifications are stored in the database and will later be used to send emails or text messages.

### Kafka Topics

#### 1. Reservation Status Notifications
- **Topic:** `reservation-status-topic`
- **Purpose:** Sends notifications to customers when their reservation request is accepted or rejected.

**Process:**
1. Consumes a `ReservationStatusMessage`.
2. Retrieves the restaurant name using `RestaurantClient`.
3. Formats the reservation time.
4. Creates a notification indicating whether the reservation was accepted or rejected.
5. Saves the notification in the database.
6. Retrieves user's info via `UserClient` and sends an email to that user.

**Notification Example:**
> Your reservation request to restaurant *Pizza Place* at time *18:30 22-11* was *accepted*. *(22-11 is day-month)*

---

#### 2. Incoming Reservation Notifications
- **Topic:** `incoming-reservation-topic`
- **Purpose:** Sends notifications to restaurant owners when a customer makes a new reservation request.

**Process:**
1. Consumes an `IncomingReservationMessage`.
2. Retrieves the restaurant information using `RestaurantClient`.
3. Retrieves customer and restaurant owner details using `UserClient`.
4. Formats the reservation request time.
5. Creates a notification for the restaurant owner.
6. Saves the notification in the database.
7. Sends email to restaurant owner

**Notification Example:**
> A reservation request was made by customer John Bitescout for time *19:00 22-11*. *(22-11 is day-month)*

---

#### 3. Special Offer Notifications
- **Topic:** `special-offer-topic`
- **Purpose:** Sends notifications to all users who have favorited a restaurant when that restaurant posts a special offer.

**Process:**
1. Consumes a `SpecialOfferMessage`.
2. Retrieves restaurant details using `RestaurantClient`.
3. Fetches a list of users who favorited the restaurant using `UserClient`.
4. Formats the offer validity period (start and end dates).
5. Creates and saves notifications for each user.

**Notification Example:**
> One of your favorite restaurants, *Burger Joint*, has a special offer! Don't miss out on this exclusive deal! Only available between *12:00 25-11* and *21:00 30-11*.

---

## NotificationController Documentation

This document provides an overview of the `NotificationController` class, which is responsible for managing notifications for users in the system. The controller provides endpoints to create, retrieve, update, and delete notifications.

### Table of Contents

- [Overview](#overview)
- [API Endpoints](#api-endpoints)
  - [Create Notification](#create-notification)
  - [Get Notifications](#get-notifications)
  - [Mark Notification as Seen](#mark-notification-as-seen)
  - [Delete Notification](#delete-notification)
- [Request & Response Formats](#request--response-formats)

### Overview

The `NotificationController` is a RESTful API controller that interacts with the `NotificationService` to manage notifications. It supports the following operations:

- **Create a notification**
- **Retrieve a list of notifications**
- **Mark a notification as seen**
- **Delete a notification**

The controller is currently set up for testing purposes, and future versions will rely on a `NotificationsConsumer` to create notifications, at which point that functionality from this controller might be removed.

### API Endpoints

#### Create Notification

- **URL**: `/api/v1/notifications`
- **Method**: `POST`
- **Description**: Creates a new notification for the user.
- **Request Headers**:
  - `User-Id` (Long): The ID of the user creating the notification.
- **Request Body**: A `NotificationRequest` object containing the details of the notification.
  - **Fields**:
    - `message` (String): The content of the notification message. It must be between 5 and 500 characters long.
    - `notificationType` (`NotificationType`): The type of the notification (e.g., `INCOMING_RESERVATION_NOTIFICATION`, `RESERVATION_STATUS_NOTIFICATION`, `SPECIAL_OFFER_NOTIFICATION`).
  - Example:
    ```json
    {
      "message": "Your reservation request to restaurant Pizza Place at time 18:30 22-11 was accepted.",
      "notificationType": "RESERVATION_STATUS_NOTIFICATION"
    }
    ```
- **Response**:
  - Status: `201 Created`
  - Body: A `NotificationResponse` object representing the newly created notification.
  - Example:
    ```json
    {
      "id": 1,
      "userId": 123,
      "message": "Your reservation request to restaurant Pizza Place at time 18:30 22-11 was accepted.",
      "notificationType": "RESERVATION_STATUS_NOTIFICATION",
      "isRead": false,
      "createdAt": "2024-11-22T14:30:00"
    }
    ```

#### Get Notifications

- **URL**: `/api/v1/notifications`
- **Method**: `GET`
- **Description**: Retrieves a list of notifications for the specified user.
- **Request Headers**:
  - `User-Id` (Long): The ID of the user whose notifications are being requested.
- **Response**:
  - Status: `200 OK`
  - Body: A list of `NotificationResponse` objects representing the user's notifications.
  - Example:
    ```json
    [
      {
        "id": 1,
        "userId": 123,
        "message": "Your reservation request to restaurant Pizza Place at time 18:30 22-11 was accepted.",
        "notificationType": "RESERVATION_STATUS_NOTIFICATION",
        "isRead": false,
        "createdAt": "2024-11-22T14:30:00"
      },
      {
        "id": 2,
        "userId": 123,
        "message": "A reservation request was made by customer John Bitescout for time 19:00 22-11",
        "notificationType": "INCOMING_RESERVATION_NOTIFICATION",
        "isRead": true,
        "createdAt": "2024-11-21T10:15:00"
      }
    ]
    ```

#### Mark Notification as Seen

- **URL**: `/api/v1/notifications/{notification-id}`
- **Method**: `PUT`
- **Description**: Marks a notification as "seen" for the specified user.
- **Request Headers**:
  - `User-Id` (Long): The ID of the user performing the action.
- **Path Variable**:
  - `notification-id` (Long): The ID of the notification to mark as seen.
- **Response**:
  - Status: `200 OK`
  - Body: A `NotificationResponse` object representing the updated notification.
  - Example:
    ```json
     {
        "id": 2,
        "userId": 123,
        "message": "A reservation request was made by customer John Bitescout for time 19:00 22-11",
        "notificationType": "INCOMING_RESERVATION_NOTIFICATION",
        "isRead": true,
        "createdAt": "2024-11-21T10:15:00"
    }
    ```

#### Delete Notification

- **URL**: `/api/v1/notifications/{notification-id}`
- **Method**: `DELETE`
- **Description**: Deletes a notification for the specified user.
- **Request Headers**:
  - `User-Id` (Long): The ID of the user performing the action.
- **Path Variable**:
  - `notification-id` (Long): The ID of the notification to be deleted.
- **Response**:
  - Status: `204 No Content`
  - Body: None.

### Request & Response Formats

#### Request Format

- **NotificationRequest**: A JSON object sent when creating a notification.
  - Fields:
    - `message` (String): The content of the notification message. It must be between 5 and 500 characters long.
    - `notificationType` (`NotificationType`): The type of notification, such as `SPECIAL_OFFER_NOTIFICATION`, `INCOMING_RESERVATION_NOTIFICATION`, `RESERVATION_STATUS_NOTIFICATION`.
  
    **Validation**:
    - `message`: Must be between 5 and 500 characters in length.
    - `notificationType`: Cannot be `null`.

  Example:
  ```json
  {
    "message": "A reservation request was made by customer John Bitescout for time 19:00 22-11",
    "notificationType": "INCOMING_RESERVATION_NOTIFICATION"
  }
  ```
#### Response Format

- **NotificationResponse**: A JSON object returned for notification operations (create, retrieve, update).
    - Fields:
        - `id` (Long): The ID of the notification.
        - `userId` (Long): The ID of the user associated with the notification.
        - `message` (String): The content of the notification message.
        - `notificationType` (`NotificationType`): The type of notification.
        - `isRead` (Boolean): Whether the notification has been marked as seen.
        - `createdAt` (String): The creation timestamp of the notification in ISO 8601 format (`YYYY-MM-DDTHH:mm:ss`).

#### Example:
```json
{
  "id": 1,
  "userId": 123,
  "message": "A reservation request was made by customer John Bitescout for time 19:00 22-11",
  "notificationType": "INCOMING_RESERVATION_NOTIFICATION",
  "isRead": false,
  "createdAt": "2024-11-22T14:30:00"
}



