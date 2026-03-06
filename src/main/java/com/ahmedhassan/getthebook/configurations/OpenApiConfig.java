package com.ahmedhassan.getthebook.configurations;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
	@Bean
	public OpenAPI apiInfo() {
		return new OpenAPI()
						.addSecurityItem(new SecurityRequirement()
										.addList("Bearer Authentication")
						)
						.components(new Components()
										.addSecuritySchemes("Bearer Authentication",
														new SecurityScheme()
																		.type(SecurityScheme.Type.HTTP)
																		.scheme("bearer")
																		.bearerFormat("JWT")
																		.description("Provide your JWT token")
										)
						)
						.info(new Info()
										.title("Book Social Network API")
										.description("REST APIs for Book Social Network application")
										.version("1.0")
										.contact(new Contact()
														.name("Ahmed Hassan")
														.email("ahmedhassan398@outlook.com")
										)
										.license(new License()
														.name("Apache 2.0")
														.url("https://www.apache.org/licenses/LICENSE-2.0.html")
										)
						).addServersItem(new Server()
										.url("/api/v1")
										.description("Version 1")
						);
	}
}