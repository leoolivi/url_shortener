package com.main.auth.services;

import com.main.auth.domain.models.AppUser;
import com.main.auth.exceptions.UserAlreadyExistException;
import com.main.auth.repositories.AppUserRepository;
import com.urlshortener.data.request.auth.RegisterRequest;
import com.urlshortener.data.request.user.DeleteUserRequest;
import com.urlshortener.data.request.user.UpdateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test suite per {@link AppUserService}.
 *
 * Copre:
 *  - Ricerca utente per email (successo e fallimento)
 *  - Creazione utente (successo e utente già esistente)
 *  - Aggiornamento utente (successo e utente non trovato)
 *  - Cancellazione utente (successo e utente non trovato)
 *  - loadUserByUsername (delegato a findUserByEmail)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AppUserService")
class AppUserServiceTest {

    @Mock
    private AppUserRepository repo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AppUserService service;

    // ---------------------------------------------------------------------------
    // Fixtures
    // ---------------------------------------------------------------------------

    private AppUser existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new AppUser(1L, "alice@example.com", "hashed_pw", "user");
    }

    // ---------------------------------------------------------------------------
    // findUserByEmail
    // ---------------------------------------------------------------------------

    @Nested
    @DisplayName("findUserByEmail")
    class FindUserByEmail {

        @Test
        @DisplayName("restituisce l'utente quando esiste")
        void returnsUser_whenFound() {
            when(repo.findByEmail("alice@example.com")).thenReturn(Optional.of(existingUser));

            AppUser result = service.findUserByEmail("alice@example.com");

            assertThat(result).isEqualTo(existingUser);
        }

        @Test
        @DisplayName("lancia UsernameNotFoundException quando l'utente non esiste")
        void throwsException_whenNotFound() {
            when(repo.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findUserByEmail("ghost@example.com"))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessage("User not found");
        }
    }

    // ---------------------------------------------------------------------------
    // createUser
    // ---------------------------------------------------------------------------

    @Nested
    @DisplayName("createUser")
    class CreateUser {

        @Test
        @DisplayName("crea e salva il nuovo utente con password hashata")
        void createsUser_withEncodedPassword() {
            RegisterRequest request = new RegisterRequest("bob@example.com", "plaintext", "user");
            when(repo.findByEmail("bob@example.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("plaintext")).thenReturn("encoded_pw");
            when(repo.save(any(AppUser.class))).thenAnswer(inv -> inv.getArgument(0));

            AppUser created = service.createUser(request);

            assertThat(created.getEmail()).isEqualTo("bob@example.com");
            assertThat(created.getPassword()).isEqualTo("encoded_pw");
            assertThat(created.getRole()).isEqualTo("user");
            verify(repo).save(any(AppUser.class));
        }

        @Test
        @DisplayName("lancia UserAlreadyExistException se l'email è già registrata")
        void throwsException_whenEmailAlreadyExists() {
            RegisterRequest request = new RegisterRequest("alice@example.com", "pw", "user");
            when(repo.findByEmail("alice@example.com")).thenReturn(Optional.of(existingUser));

            assertThatThrownBy(() -> service.createUser(request))
                    .isInstanceOf(UserAlreadyExistException.class)
                    .hasMessage("User already exists");

            verify(repo, never()).save(any());
        }
    }

    // ---------------------------------------------------------------------------
    // updateUser
    // ---------------------------------------------------------------------------

    @Nested
    @DisplayName("updateUser")
    class UpdateUser {

        @Test
        @DisplayName("aggiorna i campi non-null dell'utente")
        void updatesFields_whenPresent() {
            UpdateUserRequest request = new UpdateUserRequest(1L, "new@example.com", "new_pw", "admin");
            when(repo.findById(1L)).thenReturn(Optional.of(existingUser));
            when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            AppUser updated = service.updateUser(request);

            assertThat(updated.getEmail()).isEqualTo("new@example.com");
            assertThat(updated.getPassword()).isEqualTo("new_pw");
            assertThat(updated.getRole()).isEqualTo("admin");
        }

        @Test
        @DisplayName("non modifica i campi null nella request")
        void doesNotOverwrite_nullFields() {
            UpdateUserRequest request = new UpdateUserRequest(1L, null, null, null);
            when(repo.findById(1L)).thenReturn(Optional.of(existingUser));
            when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            AppUser updated = service.updateUser(request);

            // I valori originali devono essere intatti
            assertThat(updated.getEmail()).isEqualTo("alice@example.com");
            assertThat(updated.getPassword()).isEqualTo("hashed_pw");
            assertThat(updated.getRole()).isEqualTo("user");
        }

        @Test
        @DisplayName("lancia UsernameNotFoundException se l'utente non esiste")
        void throwsException_whenNotFound() {
            UpdateUserRequest request = new UpdateUserRequest(99L, "x@x.com", null, null);
            when(repo.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.updateUser(request))
                    .isInstanceOf(UsernameNotFoundException.class);
        }
    }

    // ---------------------------------------------------------------------------
    // deleteUser
    // ---------------------------------------------------------------------------

    @Nested
    @DisplayName("deleteUser")
    class DeleteUser {

        @Test
        @DisplayName("elimina l'utente e lo restituisce")
        void deletesAndReturnsUser() {
            DeleteUserRequest request = new DeleteUserRequest(1L);
            when(repo.findById(1L)).thenReturn(Optional.of(existingUser));

            AppUser deleted = service.deleteUser(request);

            assertThat(deleted).isEqualTo(existingUser);
            verify(repo).delete(existingUser);
        }

        @Test
        @DisplayName("lancia UsernameNotFoundException se l'utente non esiste")
        void throwsException_whenNotFound() {
            DeleteUserRequest request = new DeleteUserRequest(99L);
            when(repo.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deleteUser(request))
                    .isInstanceOf(UsernameNotFoundException.class);

            verify(repo, never()).delete(any());
        }
    }

    // ---------------------------------------------------------------------------
    // loadUserByUsername (Spring Security contract)
    // ---------------------------------------------------------------------------

    @Nested
    @DisplayName("loadUserByUsername")
    class LoadUserByUsername {

        @Test
        @DisplayName("delega a findByEmail e restituisce UserDetails")
        void returnsUserDetails_whenFound() {
            when(repo.findByEmail("alice@example.com")).thenReturn(Optional.of(existingUser));

            var details = service.loadUserByUsername("alice@example.com");

            assertThat(details.getUsername()).isEqualTo("alice@example.com");
        }

        @Test
        @DisplayName("lancia UsernameNotFoundException per email inesistente")
        void throwsException_whenNotFound() {
            when(repo.findByEmail("unknown@x.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.loadUserByUsername("unknown@x.com"))
                    .isInstanceOf(UsernameNotFoundException.class);
        }
    }
}
