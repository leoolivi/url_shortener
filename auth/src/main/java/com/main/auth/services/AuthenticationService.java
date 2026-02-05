package com.main.auth.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.main.auth.domain.models.AppUser;
import com.urlshortener.data.AuthenticateRequest;
import com.urlshortener.data.AuthenticateResponse;
import com.urlshortener.data.RegisterRequest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AppUserService detailsService;

    public AuthenticateResponse authenticate(AuthenticateRequest request) {
        var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        if (auth.isAuthenticated()) {
            AppUser userDetails = detailsService.findUserByEmail(request.email());
            SecurityContextHolder.getContext().setAuthentication(auth);
            String token = jwtService.generateToken(userDetails);
            return AuthenticateResponse.builder()
                        .email(request.email())
                        .id(userDetails.getId())
                        .token(token)
                        .role(userDetails.getAuthorities().toArray()[0].toString())
                        .isAuthenticated(true)
                        .build();
        }
        log.info("Unsuccesfully login happened");
        return AuthenticateResponse.builder()
            .email(request.email())
            .isAuthenticated(false)
            .build();
    }

    public AuthenticateResponse register(RegisterRequest request) {
        var newUser = detailsService.createUser(new RegisterRequest(request.email(), request.password(), request.role()));
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        String token = jwtService.generateToken(newUser);
        log.info("user created successfully");
        return AuthenticateResponse.builder()
                    .id(newUser.getId())
                    .email(request.email())
                    .token(token)
                    .role(newUser.getRole())
                    .isAuthenticated(true)
                    .build();
    }
} 
