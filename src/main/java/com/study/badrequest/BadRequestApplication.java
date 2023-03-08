package com.study.badrequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BadRequestApplication {

    public static void main(String[] args) {
        SpringApplication.run(BadRequestApplication.class, args);
    }


}
