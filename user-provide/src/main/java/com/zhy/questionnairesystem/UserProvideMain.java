package com.zhy.questionnairesystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@MapperScan("com.zhy.questionnairesystem.mapper")
@SpringBootApplication
@EnableEurekaClient
public class UserProvideMain {
    public static void main(String[] args) {
        SpringApplication.run(UserProvideMain.class, args);
    }
}
