package com.urlshortener.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticateResponse {
    private Long id;
    private String email;
    private String token;
    private String role;
    private boolean isAuthenticated;
}
