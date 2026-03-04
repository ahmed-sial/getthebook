package com.ahmedhassan.getthebook;

import com.ahmedhassan.getthebook.entities.Role;
import com.ahmedhassan.getthebook.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GetthebookApplication {

	public static void main(String[] args) {
		SpringApplication.run(GetthebookApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(RoleRepository roleRepository) {
		return  args -> {
			if (roleRepository.findRoleByName("USER").isEmpty()) {
				roleRepository.save(Role.builder().name("USER").build());
			}
		};
	}
}

// TODO: Auditing
// TODO: Logging
// TODO: OpenApi Docs
// TODO: Global Exception Handler
