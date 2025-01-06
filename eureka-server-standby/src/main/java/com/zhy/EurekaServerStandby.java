package com.zhy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerStandby {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerStandby.class, args);
    }
}
