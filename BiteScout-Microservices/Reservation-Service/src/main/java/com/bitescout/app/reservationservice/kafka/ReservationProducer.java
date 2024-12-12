package com.bitescout.app.reservationservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationProducer {

    private final KafkaTemplate<String, ReservationStatusMessage> kafkaTemplate;

    public void sendReservationNotification(ReservationStatusMessage payload){
        log.info("Sending reservation status payload");
        Message<ReservationStatusMessage> message = MessageBuilder
                .withPayload(payload)
                .setHeader(TOPIC, "reservation-status-topic")
                .build();

        kafkaTemplate.send(message);
    }

    public void sendIncomingReservationNotification(ReservationStatusMessage payload){
        log.info("Sending incoming reservation payload");
        Message<ReservationStatusMessage> message = MessageBuilder
                .withPayload(payload)
                .setHeader(TOPIC, "incoming-reservation-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
