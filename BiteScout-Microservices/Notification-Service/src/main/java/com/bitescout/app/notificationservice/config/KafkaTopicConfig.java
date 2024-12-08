package com.bitescout.app.notificationservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic reservationStatusTopic(){
        return TopicBuilder
                .name("reservation-status-topic")
                .build();
    }

    @Bean
    public NewTopic incomingReservationTopic(){
        return TopicBuilder
                .name("incoming-reservation-topic")
                .build();
    }

    @Bean
    public NewTopic specialOfferTopic(){
        return TopicBuilder
                .name("special-offer-topic")
                .build();
    }

    @Bean
    public NewTopic reviewInteractionTopic(){
        return TopicBuilder
                .name("review-interaction-topic")
                .build();
    }

}
