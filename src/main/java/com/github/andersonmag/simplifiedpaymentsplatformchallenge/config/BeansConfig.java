package com.github.andersonmag.simplifiedpaymentsplatformchallenge.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeansConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Simplified Payments Platform Challenge")
                        .version("1.0")
                        .description("API Simplified Payments Platform Challenge Documentation")
                        .contact(new Contact()
                                .name("Anderson Delmondes")
                                .email("andersondel.dev@gmail.com")
                                .url("https://github.com/andersonmag")));
    }
}
