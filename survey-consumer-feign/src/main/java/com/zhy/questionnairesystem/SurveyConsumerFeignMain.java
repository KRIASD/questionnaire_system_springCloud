package com.zhy.questionnairesystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SurveyConsumerFeignMain {
    public static void main(String[] args) {
        SpringApplication.run(SurveyConsumerFeignMain.class, args);
    }
}
