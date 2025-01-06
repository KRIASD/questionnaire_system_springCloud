package com.zhy.querstionnairesystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@MapperScan({"com.zhy.querstionnairesystem.mapper"})
public class SurveyProvideMain {
    public static void main(String[] args) {
        SpringApplication.run(SurveyProvideMain.class, args);
    }
}
