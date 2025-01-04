package com.bitescout.app.restaurantservice.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaSpecialOfferTopicConfig {

    @Bean
    public NewTopic reservationTopic(){
        return TopicBuilder
                .name("special-offer-topic")
                .build();
    }
}
