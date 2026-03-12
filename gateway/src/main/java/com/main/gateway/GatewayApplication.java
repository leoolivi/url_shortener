package com.main.gateway;

import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import com.main.gateway.services.InternalKeyPairService;
import com.main.gateway.services.ProducerService;
import com.urlshortener.data.MessageEnvelope;
import com.urlshortener.data.request.keys.GetInternalPublicKey;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ConfigurationPropertiesScan("com.urlshortener")
@AllArgsConstructor
@Slf4j
public class GatewayApplication implements CommandLineRunner {

	private final ProducerService producerService;
	private final InternalKeyPairService keyPairService;

	public static void main(String[] args) {
		log.info("Starting gateway application");
		SpringApplication.run(GatewayApplication.class, args);
		log.info("Gateway application started");
	}

	@Override
	public void run(String... args) throws Exception {
		keyPairService.generateStringKeyPair();
		log.info("Generated key pair");

		MessageEnvelope<GetInternalPublicKey> request = new MessageEnvelope<>();
		request.setCorrelationId(UUID.randomUUID().toString());
		request.setMessageType("GET_INTERNAL_PUBLIC_KEY_REQUEST");
		request.setSource("gateway");
		request.setTimestamp(System.currentTimeMillis());
		request.setPayload(new GetInternalPublicKey());
		
		producerService.sendMessage("auth.keys.exchange", "key.request", request);
	}

}
