package com.ahmedhassan.getthebook.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "LoginRequest", description = "Request for login of existing user")
public record LoginRequest(
				@NotBlank(message = "Email must not be blank")
				@Email(message = "Email is not well formatted")
				@Schema(description = "User email", example = "johndoe@example.com")
				String email,

				@NotBlank(message = "Password must not be blank")
				@Size(min = 8, max = 16, message = "Password must be between 8 to 16 characters")
				@Schema(description = "User password", example = "StrongP@ssword1")
				String password
) {
}