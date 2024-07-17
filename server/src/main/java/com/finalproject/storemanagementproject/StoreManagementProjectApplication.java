package com.finalproject.storemanagementproject;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StoreManagementProjectApplication {

    @Bean
    public CommandLineRunner welcome() {
        return args -> {
            System.out.println("Welcome to Store Management API");
            System.out.println("Server is running on http://localhost:8080/api");
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(StoreManagementProjectApplication.class, args);
    }
}
