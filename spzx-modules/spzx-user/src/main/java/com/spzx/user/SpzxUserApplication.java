package com.spzx.user;

import com.spzx.common.security.annotation.EnableCustomConfig;
import com.spzx.common.security.annotation.EnableRyFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRyFeignClients
@EnableCustomConfig
public class SpzxUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpzxUserApplication.class);

    }
}