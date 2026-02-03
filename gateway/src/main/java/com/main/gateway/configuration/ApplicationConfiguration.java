package com.main.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public WebClient webClient() {
        return WebClient.create("http://localhost:3002");
    }

    /* @Bean
    public UserDetailsService userDetailsService() {
        return username -> { 
            var detailsRes = webClient().post()
            .uri("/api/v1/auth/user-details")
            .bodyValue(username)
            .retrieve()
            .bodyToMono(UserDetails.class);
            return detailsRes.block();
        };
    } */
}
