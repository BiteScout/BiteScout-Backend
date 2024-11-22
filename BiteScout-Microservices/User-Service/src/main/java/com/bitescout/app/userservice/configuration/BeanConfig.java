package com.bitescout.app.userservice.configuration;

import org.modelmapper.Conditions;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Configuration;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class BeanConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.LOOSE)
                .setPropertyCondition(Conditions.isNotNull());
        return modelMapper;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}