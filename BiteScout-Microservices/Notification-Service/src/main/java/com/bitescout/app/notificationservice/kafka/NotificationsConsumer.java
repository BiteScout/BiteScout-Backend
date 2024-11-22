package com.bitescout.app.notificationservice.kafka;

import com.bitescout.app.notificationservice.kafka.offer.SpecialOfferMessage;
import com.bitescout.app.notificationservice.kafka.reservation.IncomingReservationMessage;
import com.bitescout.app.notificationservice.kafka.reservation.ReservationStatus;
import com.bitescout.app.notificationservice.kafka.reservation.ReservationStatusMessage;
import com.bitescout.app.notificationservice.notification.Notification;
import com.bitescout.app.notificationservice.notification.NotificationRepository;
import com.bitescout.app.notificationservice.notification.NotificationType;
import com.bitescout.app.notificationservice.restaurant.RestaurantClient;
import com.bitescout.app.notificationservice.restaurant.RestaurantResponse;
import com.bitescout.app.notificationservice.user.UserClient;
import com.bitescout.app.notificationservice.user.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationsConsumer {

    private final NotificationRepository repository;

    //the methods need restaurant client to get the restaurant name and the user client
    //to get the user's email and phone number information
    private final RestaurantClient restaurantClient;
    private final UserClient userClient;

    //these methods are not complete, email and text message features will be added later
    //notification goes to customer, when restaurant owner accepts/rejects their reservation request
    @KafkaListener(topics = "reservation-status-topic")
    public void consumeReservationStatusTopic(ReservationStatusMessage message){
        log.info(format("Consuming reservation status message from reservation-status-topic %s", message));

        String status = message.reservationStatus() == ReservationStatus.ACCEPTED ? "accepted" : "rejected";
        String restaurantName = restaurantClient.getRestaurant(message.restaurantId()).get().name();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM");
        String formattedTime = message.reservationTime().format(formatter);

        repository.save(Notification.builder()
                .userId(message.customerId())
                //Message subject to change
                .message(format("Your reservation request to restaurant %s at time %s was %s",
                        restaurantName, formattedTime, status))
                .notificationType(NotificationType.RESERVATION_STATUS_NOTIFICATION)
                .build());
    }

    //notification goes to restaurant owner, when someone makes a reservation request
    @KafkaListener(topics = "incoming-reservation-topic")
    public void consumerIncomingReservationTopic(IncomingReservationMessage message){
        log.info(format("Consuming incoming reservation message from incoming-reservation-topic %s",message));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM");
        String formattedTime = message.createdAt().format(formatter);

        Long ownerId = restaurantClient.getRestaurant(message.restaurantId()).get().ownerId();
        UserResponse userResponse = userClient.getUser(message.customerId());

        repository.save(Notification.builder()
                .userId(ownerId)
                //Message subject to change
                .message(format("A reservation request was made by customer id %d for time %s",
                        message.customerId(), formattedTime))
                .notificationType(NotificationType.INCOMING_RESERVATION_NOTIFICATION)
                .build());
    }

    //goes to all users that have favorited a restaurant, when that restaurant posts a special offer
    @KafkaListener(topics = "special-offer-topic")
    public void consumeSpecialOfferTopic(SpecialOfferMessage message){
        log.info("Consuming special offer message from special-offer-topic");

        RestaurantResponse restaurantResponse = restaurantClient.getRestaurant(message.restaurantId()).get();
        List<UserResponse> users = userClient.getUsersByFavoritedRestaurant(message.restaurantId());

        String restaurantName = restaurantResponse.name();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM");
        String startDate = message.startDate().format(formatter);
        String endDate = message.endDate().format(formatter);

        for(UserResponse user: users){
            repository.save(Notification.builder()
                    .userId(user.id())
                    .message(String.format(
                            "One of your favorite restaurants, %s, has a special offer!" +
                                    " Don't miss out on this exclusive deal! Only available between" +
                                    " %s and %s!", restaurantName, startDate, endDate)
                            )
                    .notificationType(NotificationType.SPECIAL_OFFER_NOTIFICATION)
                    .build());
        }
    }
}
