package com.sammy.codexhotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableMongoAuditing
@SpringBootApplication
@EnableScheduling
public class CodexhotelApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodexhotelApplication.class, args);
    }

}
