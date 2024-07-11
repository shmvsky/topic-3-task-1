package ru.shmvsky.topic3task1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class Topic3Task1Application {

    public static void main(String[] args) {
        SpringApplication.run(Topic3Task1Application.class, args);
    }

}
