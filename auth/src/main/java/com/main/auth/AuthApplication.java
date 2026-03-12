package com.main.auth;

import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.main.auth.domain.models.AppUser;
import com.main.auth.repositories.AppUserRepository;
import com.main.auth.services.ProducerService;
import com.main.auth.services.UserKeyPairService;
import com.urlshortener.data.MessageEnvelope;
import com.urlshortener.data.request.keys.GetInternalPublicKey;
import com.urlshortener.data.request.keys.UpdatedKeyEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@AllArgsConstructor
@Slf4j
public class AuthApplication implements CommandLineRunner {

	private final AppUserRepository repo;
	private final PasswordEncoder passwordEncoder;
	private final UserKeyPairService keyPairService;
	private final ProducerService producerService;

	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		repo.save(new AppUser("user@example.com", passwordEncoder.encode("password"), "user"));
		log.info("Created mock user");
		
		keyPairService.generateStringKeyPair();
		log.info("Generated key pair");

		UpdatedKeyEvent eventPayload = new UpdatedKeyEvent(keyPairService.findCurrentPublicKey());
		MessageEnvelope<UpdatedKeyEvent> event = new MessageEnvelope<>();
		event.setCorrelationId(UUID.randomUUID().toString());
		event.setMessageType("UPDATED_KEY_EVENT");
		event.setSource("auth");
		event.setTimestamp(System.currentTimeMillis());
		event.setPayload(eventPayload);	

		MessageEnvelope<GetInternalPublicKey> request = new MessageEnvelope<>();
		request.setCorrelationId(UUID.randomUUID().toString());
		request.setMessageType("GET_INTERNAL_PUBLIC_KEY_REQUEST");
		request.setSource("auth");
		request.setTimestamp(System.currentTimeMillis());
		request.setPayload(new GetInternalPublicKey());
		
		producerService.sendMessage("auth.keys.exchange", "key.request", request);
		producerService.sendMessage("auth.keys.exchange", "key.request", event);
	}

}
