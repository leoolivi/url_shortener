package com.main.auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.main.auth.domain.models.AppUser;
import com.main.auth.repositories.AppUserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@AllArgsConstructor
@Slf4j
public class AuthApplication implements CommandLineRunner {

	private final AppUserRepository repo;
	private final PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		repo.save(new AppUser("email", passwordEncoder.encode("password"), "user"));
		log.info("Created mock user");
	}

}
