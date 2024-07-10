package com.spzx.order;

import com.spzx.common.security.annotation.EnableCustomConfig;
import com.spzx.common.security.annotation.EnableRyFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCustomConfig
@EnableRyFeignClients
public class SpzxOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpzxOrderApplication.class);
    }
}