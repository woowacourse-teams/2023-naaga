package com.now.naaga;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class NaagaApplication {

    public static void main(String[] args) {
        SpringApplication.run(NaagaApplication.class, args);
    }
}
