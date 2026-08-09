package com.vivero;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ViveroApplication {

    public static void main(String[] args) {
        String dbHost = System.getenv("DB_HOST");
        String dbName = System.getenv("DB_NAME");
        String dbUrl = System.getenv("SPRING_DATASOURCE_URL");
        System.out.println(">>> [DB CONFIG] DB_HOST: " + (dbHost != null ? dbHost : "NOT SET"));
        System.out.println(">>> [DB CONFIG] DB_NAME: " + (dbName != null ? dbName : "NOT SET"));
        System.out.println(">>> [DB CONFIG] SPRING_DATASOURCE_URL: " + (dbUrl != null ? dbUrl : "NOT SET"));
        System.out.println(">>> [DB CONFIG] DB_PORT: " + System.getenv("DB_PORT"));
        System.out.println(">>> [DB CONFIG] DB_USER: " + System.getenv("DB_USER"));
        SpringApplication.run(ViveroApplication.class, args);
    }
}

