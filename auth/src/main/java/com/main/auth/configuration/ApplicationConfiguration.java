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
import com.urlshortener.security.InternalEnvBase64KeyProvider;
import com.urlshortener.security.InternalJwtVerifier;
import com.urlshortener.security.InternalKeyProvider;
import com.urlshortener.security.InternalTokenPolicyProvider;
import com.urlshortener.security.JwtProperties;
import com.urlshortener.security.UserEnvBase64KeyProvider;
import com.urlshortener.security.UserJwtSigner;
import com.urlshortener.security.UserKeyProvider;
import com.urlshortener.security.UserTokenPolicyProvider;
import com.urlshortener.utils.UserMapper;

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
    public UserJwtSigner userJwtSigner(UserKeyProvider keyProvider, UserTokenPolicyProvider tokenPolicyProvider) {
        return new UserJwtSigner(keyProvider, tokenPolicyProvider);
    }

    @Bean
    public InternalJwtVerifier internalJwtVerifier(InternalKeyProvider keyProvider, InternalTokenPolicyProvider tokenPolicyProvider) {
        return new InternalJwtVerifier(keyProvider, tokenPolicyProvider);
    }

    @Bean
    public UserKeyProvider userKeyProvider(JwtProperties jwtProperties) {
        return new UserEnvBase64KeyProvider(jwtProperties);
    }

    @Bean
    public InternalKeyProvider internalKeyProvider(JwtProperties jwtProperties) {
        return new InternalEnvBase64KeyProvider(jwtProperties);
    }

    @Bean
    public UserTokenPolicyProvider userTokenPolicyProvider(JwtProperties jwtProperties) {
        return new UserTokenPolicyProvider(jwtProperties);
    }

    @Bean
    public InternalTokenPolicyProvider internalTokenPolicyProvider(JwtProperties jwtProperties) {
        return new InternalTokenPolicyProvider(jwtProperties);
    }

    @Bean 
    public JwtProperties jwtProperties() {
        return new JwtProperties();
    }

    @Bean
    public UserMapper userMapper() {
        return new UserMapper();
    }
}
