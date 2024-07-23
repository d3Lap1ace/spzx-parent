package com.spzx.payment;

import com.spzx.common.security.annotation.EnableCustomConfig;
import com.spzx.common.security.annotation.EnableRyFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class SpzxPaymentApplication {
    public static void main(String[] args) {

        SpringApplication.run(SpzxPaymentApplication.class, args);
    }
}