package com.bitescout.app.restaurantservice.kafka;

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
public class SpecialOfferProducer {

    private final KafkaTemplate<String, SpecialOfferMessage> kafkaTemplate;

    public void sendSpecialOfferNotification(SpecialOfferMessage payload) {
        log.info("Sending special offer payload");
        Message<SpecialOfferMessage> message = MessageBuilder
                .withPayload(payload)
                .setHeader(TOPIC, "special-offer-topic")
                .build();

        kafkaTemplate.send(message);
    }
}