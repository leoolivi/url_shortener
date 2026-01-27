package com.urlshortener.messaging;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticateResponse {
    private Long id;
    private String email;
    private String token;
    private String role;
    private boolean isAuthenticated;
}
