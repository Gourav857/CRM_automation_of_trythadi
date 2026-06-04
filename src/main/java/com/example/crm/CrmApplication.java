package com.example.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching; // 🚀 FIXED: Yeh import missing tha

@SpringBootApplication
@EnableCaching // Ab yeh red nahi dikhayega aur perfect chalega
public class CrmApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrmApplication.class, args);
    }
}