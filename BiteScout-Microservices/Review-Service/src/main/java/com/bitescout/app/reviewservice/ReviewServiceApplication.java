package com.bitescout.app.reviewservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
<<<<<<< HEAD
import org.springframework.cloud.openfeign.EnableFeignClients;
=======
import org.springframework.data.mongodb.config.EnableMongoAuditing;
>>>>>>> f95371225955d7979fefc68ba3969684ce167e59

@EnableMongoAuditing
@SpringBootApplication
@EnableFeignClients
public class ReviewServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReviewServiceApplication.class, args);
    }

}
