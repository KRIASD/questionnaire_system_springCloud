package com.zhy.questionnairesystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
//激活Hystrix
@EnableCircuitBreaker
public class AdminConsumerFeignMain {
    public static void main(String[] args) {
        SpringApplication.run(AdminConsumerFeignMain.class, args);
    }
}
