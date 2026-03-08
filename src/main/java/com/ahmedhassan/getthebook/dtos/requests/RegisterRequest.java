package com.ahmedhassan.getthebook.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(name = "RegisterRequest", description = "Request for registration of new user")
public record RegisterRequest (
				@NotBlank(message = "First name must not be blank")
				@Size(min = 2, max = 20, message = "First name must be between 2 to 20 characters")
				@Schema(description = "User first name", example = "John")
				String firstName,

				@NotBlank(message = "Last name must not be blank")
				@Size(min = 2, max = 20, message = "Last name must be between 2 to 20 characters")
				@Schema(description = "User last name", example = "Doe")
				String lastName,

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