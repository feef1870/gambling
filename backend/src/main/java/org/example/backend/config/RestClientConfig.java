package org.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient aiDealerRestClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:8000")
                .build();
    }
}
