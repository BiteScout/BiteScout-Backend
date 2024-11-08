package com.bitescout.app.notificationservice.kafka;

import com.bitescout.app.notificationservice.kafka.reservation.ReservationStatusMessage;
import com.bitescout.app.notificationservice.kafka.reservation.ReservationStatus;
import com.bitescout.app.notificationservice.notification.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationsConsumer {

    private final NotificationService service;
    private final NotificationRepository repository;

    @KafkaListener(topics = "reservation-status-topic")
    public void consumeReservationTopic(ReservationStatusMessage message){
        log.info(format("Consuming reservation message from reservation topic %s", message));

        //send to user when reservation is accepted/rejected
        String status = message.reservationStatus() == ReservationStatus.ACCEPTED ? "accepted" : "rejected";
        repository.save(Notification.builder()
                .userId(message.customerId())
                .message(String.format("Reservation request with restaurant id %d was %s",
                        message.restaurantId(), status))
                .notificationType(NotificationType.RESERVATION_STATUS_NOTIFICATION)
                .build());
    }

    //incoming reservation topic


//    @KafkaListener(topics = "offer-topic")
//    public void consumeSpecialOfferTopic(OfferNotification offerNotification, @RequestHeader(value = "User-Id") Long userId){
//        log.info("Consuming message from offer-topic");
//        //this part will change
//        NotificationRequest request = new NotificationRequest(
//                String.format(
//                "One of your favorite restaurants, %s, has a special offer!" +
//                " Don't miss out on this exclusive deal!", offerNotification.restaurantName()),
//                NotificationType.SPECIAL_OFFER_NOTIFICATION
//        );
//
//        service.createNotification(request, userId);
//    }
}
