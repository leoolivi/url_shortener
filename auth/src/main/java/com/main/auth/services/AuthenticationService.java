package com.main.auth.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.main.auth.domain.models.AppUser;
import com.urlshortener.messaging.AuthenticateRequest;
import com.urlshortener.messaging.AuthenticateResponse;
import com.urlshortener.messaging.RegisterRequest;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AppUserService detailsService;
    private final PasswordEncoder passwordEncoder;

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
        return AuthenticateResponse.builder()
            .email(request.email())
            .isAuthenticated(false)
            .build();
    }

    public AuthenticateResponse register(RegisterRequest request) {
        var newUser = detailsService.createUser(new RegisterRequest(request.email(), passwordEncoder.encode(request.password()), request.role()));
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        String token = jwtService.generateToken(newUser);
        return AuthenticateResponse.builder()
                    .email(request.email())
                    .token(token)
                    .role(newUser.getRole())
                    .isAuthenticated(true)
                    .build();

    }
} 
