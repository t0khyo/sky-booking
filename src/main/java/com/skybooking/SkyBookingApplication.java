package com.skybooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class SkyBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkyBookingApplication.class, args);
    }

}
