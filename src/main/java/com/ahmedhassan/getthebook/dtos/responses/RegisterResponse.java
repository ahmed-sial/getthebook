package com.ahmedhassan.getthebook.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(name = "RegisterResponse", description = "User details on successful registration of user")
public record RegisterResponse(
				@Schema(description = "User unique ID", example = "550e8400-e29b-41d4-a716-446655440000")
				UUID id,

				@Schema(description = "User first name", example = "John")
				String firstName,

				@Schema(description = "User last name", example = "Doe")
				String lastName,

				@Schema(description = "User email", example = "johndoe@example.com")
				String email
) {
}