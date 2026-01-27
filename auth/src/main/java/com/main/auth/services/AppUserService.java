package com.main.auth.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.main.auth.domain.models.AppUser;
import com.main.auth.exceptions.UserAlreadyExistException;
import com.main.auth.repositories.AppUserRepository;
import com.urlshortener.messaging.DeleteUserRequest;
import com.urlshortener.messaging.RegisterRequest;
import com.urlshortener.messaging.UpdateUserRequest;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final AppUserRepository repo;
    private final PasswordEncoder passwordEncoder;
    
    public AppUser findUserByEmail(String email) {
        return repo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public AppUser createUser(RegisterRequest request) {
        if (repo.findByEmail(request.email()).isPresent()) throw new UserAlreadyExistException("User already exists");
        AppUser newUser = new AppUser(request.email(), passwordEncoder.encode(request.password()), request.role());
        repo.save(newUser);
        return newUser;
    }

    @Transactional
    public AppUser updateUser(UpdateUserRequest request) {
        var user = repo.findById(request.id()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (request.email() != null) user.setEmail(request.email());
        if (request.password() != null) user.setPassword(request.password());
        if (request.role() != null) user.setRole(request.role());
        repo.save(user);
        return user;
    }

    public AppUser deleteUser(DeleteUserRequest request) {
        var user = repo.findById(request.id()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        repo.delete(user);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
