package com.ahmedhassan.getthebook.dtos.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Login Request Tests")
public class LoginRequestTest {
	private static Validator validator;

	@BeforeAll
	static void setUpValidator() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	// VALID REQUEST
	@Test
	@DisplayName("Valid request should have no violations")
	void loginRequest_WithAllValidFields_ShouldPassValidation() {
		LoginRequest request = new LoginRequest(
						"johndoe@example.com",
						"johndoe123"
		);
		assertThat(validator.validate(request)).isEmpty();
	}

	// EMAIL
	@Test
	@DisplayName("Null email should fail @NotBlank")
	void LoginRequest_WithNullEmail_ShouldFailValidation() {
		LoginRequest request = new LoginRequest(
						null,
						"johndoe123"
		);
		Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("email");
	}

	@Test
	@DisplayName("Blank email should fail @NotBlank")
	void LoginRequest_WithBlankEmail_ShouldFailValidation() {
		LoginRequest request = new LoginRequest(
						"   ",
						"johndoe123"
		);
		Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsSequence("email", "email");
	}

	@Test
	@DisplayName("Empty email should fail @NotBlank")
	void LoginRequest_WithEmptyEmail_ShouldFailValidation() {
		LoginRequest request = new LoginRequest(
						"",
						"johndoe123"
		);
		Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("email");
	}

	@Test
	@DisplayName("Email missing '@' should fail @Email")
	void LoginRequest_WithEmailMissingAtSign_ShouldFailValidation() {
		LoginRequest request = new LoginRequest(
						"johndoeexample.com",
						"StrongP@ss1"
		);
		Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("email");
	}

	@Test
	@DisplayName("Email missing domain should fail @Email")
	void LoginRequest_WithEmailMissingDomain_ShouldFailValidation() {
		LoginRequest request = new LoginRequest(
						"johndoe@",
						"StrongP@ss1"
		);
		Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("email");
	}

	@Test
	@DisplayName("Email missing local part should fail @Email")
	void LoginRequest_WithEmailMissingLocalPart_ShouldFailValidation() {
		LoginRequest request = new LoginRequest(
						"@example.com",
						"StrongP@ss1"
		);
		Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("email");
	}

	@Test
	@DisplayName("Email with plain text should fail @Email")
	void LoginRequest_WithPlainTextEmail_ShouldFailValidation() {
		LoginRequest request = new LoginRequest(
						"notAnEmail",
						"StrongP@ss1"
		);
		Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("email");
	}

	// PASSWORD
	@Test
	@DisplayName("Null password should fail @NotBlank")
	void LoginRequest_WithNullPassword_ShouldFailValidation() {
		LoginRequest request = new LoginRequest(
						"johndoe@example.com",
						null
		);
		Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("password");
	}

	@Test
	@DisplayName("Blank password should fail @NotBlank")
	void LoginRequest_WithBlankPassword_ShouldFailValidation() {
		LoginRequest request = new LoginRequest(
						"johndoe@example.com",
						"   "
		);
		Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsSequence("password", "password");
	}

	@Test
	@DisplayName("Empty password should fail @NotBlank")
	void LoginRequest_WithEmptyPassword_ShouldFailValidation() {
		LoginRequest request = new LoginRequest(
						"johndoe@example.com",
						""
		);
		Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsSequence("password", "password");
	}

	@Test
	@DisplayName("Short password (7 chars) should fail @Size(min = 8)")
	void LoginRequest_WithShortPassword_ShouldFailValidation() {
		LoginRequest request = new LoginRequest(
						"johndoe@example.com",
						"johnnnn"
		);
		Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("password");
	}

	@Test
	@DisplayName("Long password (17 chars) should fail @Size(max = 16)")
	void LoginRequest_WithLongPassword_ShouldFailValidation() {
		LoginRequest request = new LoginRequest(
						"johndoe@example.com",
						"johndoejohndoejoh"
		);
		Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("password");
	}


	@Test
	@DisplayName("Password at min boundary (8 chars) should pass validation")
	void LoginRequest_WithPasswordAtMinBoundary_ShouldPassValidation() {
		LoginRequest request = new LoginRequest(
						"johndoe@example.com",
						"Str0ng@1"   // exactly 8 chars
		);
		assertThat(validator.validate(request)).isEmpty();
	}

	@Test
	@DisplayName("Password at max boundary (16 chars) should pass validation")
	void LoginRequest_WithPasswordAtMaxBoundary_ShouldPassValidation() {
		LoginRequest request = new LoginRequest(
						"johndoe@example.com",
						"StrongP@ssword12"   // exactly 16 chars
		);
		assertThat(validator.validate(request)).isEmpty();
	}

	// MULTIPLE VIOLATIONS
	@Test
	@DisplayName("All null fields should produce violations on all fields")
	void LoginRequest_WithAllNullFields_ShouldFailValidationOnAllFields() {
		LoginRequest request = new LoginRequest(null, null);
		Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactlyInAnyOrder("email", "password");
	}

}