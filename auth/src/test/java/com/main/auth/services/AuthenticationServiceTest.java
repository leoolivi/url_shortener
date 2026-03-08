package com.main.auth.services;

import com.main.auth.domain.models.AppUser;
import com.main.auth.exceptions.UserAlreadyExistException;
import com.urlshortener.data.request.auth.AuthenticateRequest;
import com.urlshortener.data.request.auth.RegisterRequest;
import com.urlshortener.data.response.auth.AuthenticateResponse;
import com.urlshortener.security.jwt.signer.JwtSigner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test suite per {@link AuthenticationService}.
 *
 * Copre:
 *  - authenticate: credenziali valide → token restituito
 *  - authenticate: credenziali errate → eccezione propagata
 *  - register: nuovo utente → token restituito
 *  - register: email duplicata → UserAlreadyExistException
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationService")
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtSigner jwtSigner;

    @Mock
    private AppUserService detailsService;

    @InjectMocks
    private AuthenticationService service;

    private AppUser mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new AppUser(1L, "alice@example.com", "hashed_pw", "user");
    }

    // ---------------------------------------------------------------------------
    // authenticate
    // ---------------------------------------------------------------------------

    @Nested
    @DisplayName("authenticate")
    class Authenticate {

        @Test
        @DisplayName("restituisce AuthenticateResponse con token se le credenziali sono valide")
        void returnsTokenResponse_onValidCredentials() throws Exception {
            Authentication auth = mock(Authentication.class);
            when(auth.isAuthenticated()).thenReturn(true);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(auth);
            when(detailsService.findUserByEmail("alice@example.com")).thenReturn(mockUser);
            when(jwtSigner.signToken(any())).thenReturn("jwt.token.here");

            AuthenticateResponse response = service.authenticate(
                    new AuthenticateRequest("alice@example.com", "password"));

            assertThat(response.isAuthenticated()).isTrue();
            assertThat(response.getToken()).isEqualTo("jwt.token.here");
            assertThat(response.getEmail()).isEqualTo("alice@example.com");
            assertThat(response.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("restituisce risposta non autenticata se l'autenticazione fallisce")
        void returnsNotAuthenticated_whenAuthFails() throws Exception {
            Authentication auth = mock(Authentication.class);
            when(auth.isAuthenticated()).thenReturn(false);
            when(authenticationManager.authenticate(any())).thenReturn(auth);

            AuthenticateResponse response = service.authenticate(
                    new AuthenticateRequest("alice@example.com", "wrong_pw"));

            assertThat(response.isAuthenticated()).isFalse();
            assertThat(response.getToken()).isNull();
        }

        @Test
        @DisplayName("propaga BadCredentialsException dall'AuthenticationManager")
        void propagatesException_whenManagerThrows() {
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> service.authenticate(
                    new AuthenticateRequest("alice@example.com", "wrong")))
                    .isInstanceOf(BadCredentialsException.class);
        }
    }

    // ---------------------------------------------------------------------------
    // register
    // ---------------------------------------------------------------------------

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("crea l'utente, autentica e restituisce il token")
        void createsAndAuthenticates_returnsToken() throws Exception {
            RegisterRequest request = new RegisterRequest("bob@example.com", "pw", "user");
            AppUser newUser = new AppUser(2L, "bob@example.com", "hashed", "user");

            when(detailsService.createUser(any(RegisterRequest.class))).thenReturn(newUser);
            when(jwtSigner.signToken(any())).thenReturn("new.jwt.token");

            AuthenticateResponse response = service.register(request);

            assertThat(response.isAuthenticated()).isTrue();
            assertThat(response.getToken()).isEqualTo("new.jwt.token");
            assertThat(response.getEmail()).isEqualTo("bob@example.com");
            assertThat(response.getId()).isEqualTo(2L);
            // verifica che l'autenticazione venga effettuata dopo la registrazione
            verify(authenticationManager).authenticate(any());
        }

        @Test
        @DisplayName("propaga UserAlreadyExistException se l'email è già registrata")
        void propagatesException_whenEmailDuplicated() {
            RegisterRequest request = new RegisterRequest("alice@example.com", "pw", "user");
            when(detailsService.createUser(any())).thenThrow(
                    new UserAlreadyExistException("User already exists"));

            assertThatThrownBy(() -> service.register(request))
                    .isInstanceOf(UserAlreadyExistException.class)
                    .hasMessage("User already exists");

            verify(authenticationManager, never()).authenticate(any());
        }
    }
}
