package com.main.auth.controllers;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.auth.services.AppUserService;
import com.main.auth.services.AuthenticationService;
import com.urlshortener.data.request.auth.AuthenticateRequest;
import com.urlshortener.data.request.auth.RegisterRequest;
import com.urlshortener.data.response.auth.AuthenticateResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor @Slf4j
public class AuthController {

    private final AppUserService userService;
    private final AuthenticationService authService;

    @PostMapping("login")
    public AuthenticateResponse login(@RequestBody AuthenticateRequest request) throws Exception {
        var response = authService.authenticate(request);
        log.info("Authenticated user");
        return response;
    }
    
    @PostMapping("register") 
    public AuthenticateResponse register(@RequestBody RegisterRequest request) throws Exception {
        var response = authService.register(request);
        return response;
    }

    @PostMapping("user-details")
    public UserDetails getUserDetails(@RequestBody String email) {
        return userService.findUserByEmail(email);
    }
    
}
