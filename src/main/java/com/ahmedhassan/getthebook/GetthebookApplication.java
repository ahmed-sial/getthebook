package com.ahmedhassan.getthebook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "applicationAuditAware")
public class GetthebookApplication {

	public static void main(String[] args) {
		SpringApplication.run(GetthebookApplication.class, args);
	}

}

// TODO: Logging
// TODO: OpenApi Docs
// TODO: Global Exception Handler
// TODO: Add Constraint to Entities
