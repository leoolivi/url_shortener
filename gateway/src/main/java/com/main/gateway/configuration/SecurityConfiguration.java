package com.main.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .authorizeHttpRequests(
                auth -> auth.anyRequest().permitAll()
            )
            .formLogin(Customizer.withDefaults());

        return http.build();
    } 
}
