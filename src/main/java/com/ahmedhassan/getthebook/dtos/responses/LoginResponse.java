package com.ahmedhassan.getthebook.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(name = "RegisterResponse", description = "General response for system user")
public record LoginResponse(
				@Schema(description = "User unique ID")
				UUID id,
				@Schema(description = "User first name", example = "John")
				String firstName,
				@Schema(description = "User last name", example = "Doe")
				String lastName,
				@Schema(description = "User email", example = "johndoe@example.com")
				String email,
				@Schema(description = "JWT token")
				String token
) {
}