package com.main.auth.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.main.auth.domain.models.AppUser;
import com.urlshortener.data.request.auth.AuthenticateRequest;
import com.urlshortener.data.request.auth.RegisterRequest;
import com.urlshortener.data.response.auth.AuthenticateResponse;
import com.urlshortener.data.response.user.UserResponse;
import com.urlshortener.security.UserJwtSigner;
import com.urlshortener.utils.UserMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final UserJwtSigner jwtSigner;
    private final AppUserService detailsService;

    public AuthenticateResponse authenticate(AuthenticateRequest request) throws Exception {
        // TODO: Add authentication error handling for invalid credentials
        var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        if (auth.isAuthenticated()) {
            AppUser userDetails = detailsService.findUserByEmail(request.email());
            UserResponse user = new UserResponse(userDetails.getId(), userDetails.getEmail(), userDetails.getPassword(), userDetails.getRole());
            SecurityContextHolder.getContext().setAuthentication(auth);
            String token = jwtSigner.signToken(userMapper.fromUserResponseToClaims(user));
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

    public AuthenticateResponse register(RegisterRequest request) throws Exception {
        var newUser = detailsService.createUser(new RegisterRequest(request.email(), request.password(), request.role()));
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserResponse user = new UserResponse(newUser.getId(), newUser.getEmail(), newUser.getPassword(), newUser.getRole());
        String token = jwtSigner.signToken(userMapper.fromUserResponseToClaims(user));
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
