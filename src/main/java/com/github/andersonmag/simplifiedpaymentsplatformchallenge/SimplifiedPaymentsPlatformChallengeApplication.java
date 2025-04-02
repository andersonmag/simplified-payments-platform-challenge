package com.github.andersonmag.simplifiedpaymentsplatformchallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SimplifiedPaymentsPlatformChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimplifiedPaymentsPlatformChallengeApplication.class, args);
    }
}
