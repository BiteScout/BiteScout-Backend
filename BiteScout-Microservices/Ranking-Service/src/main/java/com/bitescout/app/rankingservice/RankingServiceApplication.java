package com.bitescout.app.rankingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class RankingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RankingServiceApplication.class, args);
    }

}
