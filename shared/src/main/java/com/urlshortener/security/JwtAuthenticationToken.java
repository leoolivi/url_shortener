package com.urlshortener.security;

import java.util.Collection;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.urlshortener.data.user.UserClaims;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String token;
    private final UserClaims principal;

    public JwtAuthenticationToken(UserClaims details, String token, Collection<? extends GrantedAuthority> auth) {
        super(auth);
        this.principal = details;
        this.token = token;
        super.setAuthenticated(true);
    }    

    @Override
    public @Nullable Object getCredentials() {
        return null;
    }
    
}
