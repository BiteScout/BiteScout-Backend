package com.bitescout.app.notificationservice.kafka;

import com.bitescout.app.notificationservice.email.EmailService;
import com.bitescout.app.notificationservice.kafka.offer.SpecialOfferMessage;
import com.bitescout.app.notificationservice.kafka.reservation.ReservationStatus;
import com.bitescout.app.notificationservice.kafka.reservation.ReservationStatusMessage;
import com.bitescout.app.notificationservice.kafka.review.ReviewInteractionMessage;
import com.bitescout.app.notificationservice.notification.Notification;
import com.bitescout.app.notificationservice.notification.NotificationRepository;
import com.bitescout.app.notificationservice.notification.NotificationType;
import com.bitescout.app.notificationservice.restaurant.RestaurantClient;
import com.bitescout.app.notificationservice.restaurant.RestaurantResponse;
import com.bitescout.app.notificationservice.review.ReviewClient;
import com.bitescout.app.notificationservice.review.ReviewResponse;
import com.bitescout.app.notificationservice.user.UserClient;
import com.bitescout.app.notificationservice.user.UserResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

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
    private final ReviewClient reviewClient;
    private final EmailService emailService;

    //these methods are not complete, email and text message features will be added later
    //notification goes to customer, when restaurant owner accepts/rejects their reservation request
    @KafkaListener(topics = "reservation-status-topic")
    public void consumeReservationStatusTopic(ReservationStatusMessage message) throws MessagingException {
        log.info(format("Consuming reservation status message from reservation-status-topic %s", message));

        String status = message.reservationStatus() == ReservationStatus.ACCEPTED ? "accepted" : "rejected";
        String restaurantName = restaurantClient.getRestaurant(String.valueOf(message.restaurantId())).get().getName();
        UserResponse userResponse = userClient.getUser(String.valueOf(message.customerId())).get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM HH:mm", Locale.ENGLISH);
        String formattedTime = message.reservationTime().format(formatter);

        repository.save(Notification.builder()
                .userId(String.valueOf(message.customerId()))
                //Message subject to change
                .message(format("Your reservation request with id %d to restaurant %s at time %s was %s",
                        message.id(), restaurantName, formattedTime, status))
                .notificationType(NotificationType.RESERVATION_STATUS_NOTIFICATION)
                .build());

        String customerName;

        if(userResponse.userDetails() == null) {
            customerName = userResponse.username();
        }
        else {
            customerName = userResponse.userDetails().firstName() + " " + userResponse.userDetails().lastName();
        }

        emailService.sendReservationStatusEmail(
                userResponse.email(),
                customerName,
                restaurantName,
                status,
                formattedTime,
                message.id()
);
    }

    //notification goes to restaurant owner, when someone makes a reservation request
    @KafkaListener(topics = "incoming-reservation-topic")
    public void consumerIncomingReservationTopic(ReservationStatusMessage message) throws MessagingException {
        log.info(format("Consuming incoming reservation message from incoming-reservation-topic %s",message));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM HH:mm", Locale.ENGLISH);
        String formattedTime = message.reservationTime().format(formatter);

        RestaurantResponse response = restaurantClient.getRestaurant(String.valueOf(message.restaurantId())).get();
        String restaurantName = response.getName();
        UserResponse owner = userClient.getUser(String.valueOf(response.getOwnerId())).get(); //this is restaurant owner
        UserResponse customer = userClient.getUser(String.valueOf(message.customerId())).get();

        String ownerName;
        String customerName;

        if(owner.userDetails() == null) {
            ownerName = owner.username();
        }
        else {
            ownerName = owner.userDetails().firstName() + " " + owner.userDetails().lastName();
        }

        if(customer.userDetails() == null) {
            customerName = customer.username();
        }
        else {
            customerName = customer.userDetails().firstName() + " " + customer.userDetails().lastName();
        }

        repository.save(Notification.builder()
                .userId(String.valueOf(response.getOwnerId()))
                //Message subject to change
                .message(format("A reservation request with id %d to your restaurant %s was made by " +
                                "%s for time %s",
                        message.id(),
                        restaurantName,
                        customerName,
                        formattedTime))
                .notificationType(NotificationType.INCOMING_RESERVATION_NOTIFICATION)
                .build());

        emailService.sendIncomingReservationEmail(
                owner.email(),
                ownerName,
                restaurantName,
                customerName,
                formattedTime,
                message.id()
        );
    }

    //goes to all users that have favorited a restaurant, when that restaurant posts a special offer
    @KafkaListener(topics = "special-offer-topic")
    public void consumeSpecialOfferTopic(SpecialOfferMessage message){
        log.info("Consuming special offer message from special-offer-topic");

        List<UserResponse> users = userClient.getUsersByFavoritedRestaurant(String.valueOf(message.restaurantId()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM", Locale.ENGLISH);
        String startDate = message.startDate().format(formatter);
        String endDate = message.endDate().format(formatter);


        for(UserResponse user: users){
            repository.save(Notification.builder()
                    .userId(String.valueOf(user.id()))
                    .message(String.format(
                            "One of your favorite restaurants, %s, has a special offer between" +
                                    " %s and %s: %s, %s", message.restaurantName(), startDate, endDate,
                                    message.title(), message.description())
                            )
                    .notificationType(NotificationType.SPECIAL_OFFER_NOTIFICATION)
                    .build());
        }
    }

    @KafkaListener(topics = "review-interaction-topic")
    public void consumeReviewInteractionTopic(ReviewInteractionMessage message){
        log.info("Consuming review message from review-interaction-topic");

        //x replied :

        ReviewResponse reviewResponse = reviewClient.getReview(message.reviewId()).get();
        UserResponse reviewOwnerUser = userClient.getUser(String.valueOf(reviewResponse.customerId())).get();
        UserResponse interactingUser = userClient.getUser(String.valueOf(message.userId())).get();


        repository.save(Notification.builder()
                .userId(String.valueOf(reviewOwnerUser.id()))
                .message(String.format(
                        "%s %s has %s your comment: %s",
                        interactingUser.userDetails().firstName(),
                        interactingUser.userDetails().lastName(),
                        message.interactionType().getTemplatePhrase(),
                        message.replyText()
                )).build());

    }
}
