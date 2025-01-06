package com.zhy.questionnairesystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
//激活Feign
@EnableFeignClients
//激活Hystrix
@EnableCircuitBreaker
public class UserConsumerFeignMain {
    public static void main(String[] args) {
        SpringApplication.run(UserConsumerFeignMain.class, args);
    }
}
