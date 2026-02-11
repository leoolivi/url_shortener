package com.main.auth.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.main.auth.repositories.AppUserRepository;
import com.urlshortener.security.JwtSigner;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class ApplicationConfiguration {

    private final AppUserRepository repo;
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(AppUserRepository repo) {
        return email -> {
            return repo.findByEmail(email).orElseThrow(()  -> new UsernameNotFoundException("User not found"));
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider(userDetailsService(repo));
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public JwtSigner jwtSigner() {
        return new JwtSigner();
    }
}
