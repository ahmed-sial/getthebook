package com.ahmedhassan.getthebook.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest (
				@NotBlank(message = "First name must not be blank")
				@Size(min = 2, max = 20, message = "First name must be between 2 to 20 characters")
				String firstName,

				@NotBlank(message = "Last name must not be blank")
				@Size(min = 2, max = 20, message = "Last name must be between 2 to 20 characters")
				String lastName,

				@NotBlank(message = "Email must not be blank")
				@Email(message = "Email is not well formatted")
				String email,

				@NotBlank(message = "Password must not be blank")
				@Size(min = 8, max = 16, message = "Password must be between 8 to 16 characters")
				String password
) {
}