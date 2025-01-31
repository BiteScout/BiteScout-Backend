package com.bitescout.app.reservationservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaReservationTopicConfig {

    @Bean
    public NewTopic reservationTopic(){
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
}
