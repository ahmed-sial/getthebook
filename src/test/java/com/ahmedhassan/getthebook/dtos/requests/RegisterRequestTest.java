package com.ahmedhassan.getthebook.dtos.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Register Request Tests")
public class RegisterRequestTest {

	private static Validator validator;

	@BeforeAll
	static void setUpValidator() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	// VALID REQUEST
	@Test
	@DisplayName("Valid request should have no violations")
	void RegisterRequest_WithAllValidFields_ShouldPassValidation() {
		RegisterRequest request = new RegisterRequest(
						"John",
						"Doe",
						"johndoe@example.com",
						"StrongP@ss1"
		);
		assertThat(validator.validate(request)).isEmpty();
	}

	// FIRST NAME

	@Test
	@DisplayName("Null first name should fail @NotBlank")
	void RegisterRequest_WithNullFirstName_ShouldFailValidation() {
		RegisterRequest request = new RegisterRequest(
						null,
						"Doe",
						"johndoe@example.com",
						"StrongP@ss1"
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("firstName");
	}

	@Test
	@DisplayName("Blank first name should fail @NotBlank")
	void RegisterRequest_WithBlankFirstName_ShouldFailValidation() {
		RegisterRequest request = new RegisterRequest(
						"   ",
						"Doe",
						"johndoe@example.com",
						"StrongP@ss1"
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("firstName");
	}

	@Test
	@DisplayName("Empty first name should fail @NotBlank")
	void RegisterRequest_WithEmptyFirstName_ShouldFailValidation() {
		RegisterRequest request = new RegisterRequest(
						"",
						"Doe",
						"johndoe@example.com",
						"StrongP@ss1"
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsSequence("firstName", "firstName");
	}

	@Test
	@DisplayName("Short first name (1 char) should fail @Size(min = 2)")
	void RegisterRequest_WithShortFirstName_ShouldFailValidation() {
		RegisterRequest request = new RegisterRequest(
						"J",
						"Doe",
						"johndoe@example.com",
						"StrongP@ss1"
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("firstName");
	}

	@Test
	@DisplayName("Long first name (21 chars) should fail @Size(max = 20)")
	void RegisterRequest_WithLongFirstName_ShouldFailValidation() {
		RegisterRequest request = new RegisterRequest(
						"JohnDoeVeryVeryLongNm",   // 21 chars
						"Doe",
						"johndoe@example.com",
						"StrongP@ss1"
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("firstName");
	}

	@Test
	@DisplayName("First name at min boundary (2 chars) should pass validation")
	void RegisterRequest_WithFirstNameAtMinBoundary_ShouldPassValidation() {
		RegisterRequest request = new RegisterRequest(
						"Jo",
						"Doe",
						"johndoe@example.com",
						"StrongP@ss1"
		);
		assertThat(validator.validate(request)).isEmpty();
	}

	@Test
	@DisplayName("First name at max boundary (20 chars) should pass validation")
	void RegisterRequest_WithFirstNameAtMaxBoundary_ShouldPassValidation() {
		RegisterRequest request = new RegisterRequest(
						"JohnDoeVeryVeryLongN",  // exactly 20 chars
						"Doe",
						"johndoe@example.com",
						"StrongP@ss1"
		);
		assertThat(validator.validate(request)).isEmpty();
	}

	// LAST NAME
	@Test
	@DisplayName("Null last name should fail @NotBlank")
	void RegisterRequest_WithNullLastName_ShouldFailValidation() {
		RegisterRequest request = new RegisterRequest(
						"John",
						null,
						"johndoe@example.com",
						"StrongP@ss1"
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("lastName");
	}

	@Test
	@DisplayName("Blank last name should fail @NotBlank")
	void RegisterRequest_WithBlankLastName_ShouldFailValidation() {
		RegisterRequest request = new RegisterRequest(
						"John",
						"   ",
						"johndoe@example.com",
						"StrongP@ss1"
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("lastName");
	}

	@Test
	@DisplayName("Empty last name should fail @NotBlank")
	void RegisterRequest_WithEmptyLastName_ShouldFailValidation() {
		RegisterRequest request = new RegisterRequest(
						"John",
						"",
						"johndoe@example.com",
						"StrongP@ss1"
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsSequence("lastName", "lastName");
	}

	@Test
	@DisplayName("Short last name (1 char) should fail @Size(min = 2)")
	void RegisterRequest_WithShortLastName_ShouldFailValidation() {
		RegisterRequest request = new RegisterRequest(
						"John",
						"D",
						"johndoe@example.com",
						"StrongP@ss1"
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("lastName");
	}

	@Test
	@DisplayName("Long last name (21 chars) should fail @Size(max = 20)")
	void RegisterRequest_WithLongLastName_ShouldFailValidation() {
		RegisterRequest request = new RegisterRequest(
						"John",
						"DoeVeryVeryVeryLongNm",  // 21 chars
						"johndoe@example.com",
						"StrongP@ss1"
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("lastName");
	}

	@Test
	@DisplayName("Last name at min boundary (2 chars) should pass validation")
	void RegisterRequest_WithLastNameAtMinBoundary_ShouldPassValidation() {
		RegisterRequest request = new RegisterRequest(
						"John",
						"Do",
						"johndoe@example.com",
						"StrongP@ss1"
		);
		assertThat(validator.validate(request)).isEmpty();
	}

	@Test
	@DisplayName("Last name at max boundary (20 chars) should pass validation")
	void RegisterRequest_WithLastNameAtMaxBoundary_ShouldPassValidation() {
		RegisterRequest request = new RegisterRequest(
						"John",
						"DoeVeryVeryVeryLongN",  // exactly 20 chars
						"johndoe@example.com",
						"StrongP@ss1"
		);
		assertThat(validator.validate(request)).isEmpty();
	}

	// EMAIL
	@Test
	@DisplayName("Null email should fail @NotBlank")
	void RegisterRequest_WithNullEmail_ShouldFailValidation() {
		RegisterRequest request = new RegisterRequest(
						"John",
						"Doe",
						null,
						"StrongP@ss1"
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("email");
	}

	@Test
	@DisplayName("Blank email should fail @NotBlank")
	void RegisterRequest_WithBlankEmail_ShouldFailValidation() {
		RegisterRequest request = new RegisterRequest(
						"John",
						"Doe",
						"   ",
						"StrongP@ss1"
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsSequence("email", "email");
	}

	@Test
	@DisplayName("Empty email should fail @NotBlank")
	void RegisterRequest_WithEmptyEmail_ShouldFailValidation() {
		RegisterRequest request = new RegisterRequest(
						"John",
						"Doe",
						"",
						"StrongP@ss1"
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("email");
	}

	@Test
	@DisplayName("Email missing '@' should fail @Email")
	void RegisterRequest_WithEmailMissingAtSign_ShouldFailValidation() {
		RegisterRequest request = new RegisterRequest(
						"John",
						"Doe",
						"johndoeexample.com",
						"StrongP@ss1"
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("email");
	}

	@Test
	@DisplayName("Email missing domain should fail @Email")
	void RegisterRequest_WithEmailMissingDomain_ShouldFailValidation() {
		RegisterRequest request = new RegisterRequest(
						"John",
						"Doe",
						"johndoe@",
						"StrongP@ss1"
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("email");
	}

	@Test
	@DisplayName("Email missing local part should fail @Email")
	void RegisterRequest_WithEmailMissingLocalPart_ShouldFailValidation() {
		RegisterRequest request = new RegisterRequest(
						"John",
						"Doe",
						"@example.com",
						"StrongP@ss1"
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("email");
	}

	@Test
	@DisplayName("Email with plain text should fail @Email")
	void RegisterRequest_WithPlainTextEmail_ShouldFailValidation() {
		RegisterRequest request = new RegisterRequest(
						"John",
						"Doe",
						"notAnEmail",
						"StrongP@ss1"
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("email");
	}

	// PASSWORD
	@Test
	@DisplayName("Null password should fail @NotBlank")
	void RegisterRequest_WithNullPassword_ShouldFailValidation() {
		RegisterRequest request = new RegisterRequest(
						"John",
						"Doe",
						"johndoe@example.com",
						null
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("password");
	}

	@Test
	@DisplayName("Blank password should fail @NotBlank")
	void RegisterRequest_WithBlankPassword_ShouldFailValidation() {
		RegisterRequest request = new RegisterRequest(
						"John",
						"Doe",
						"johndoe@example.com",
						"   "
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsSequence("password", "password");
	}

	@Test
	@DisplayName("Empty password should fail @NotBlank")
	void RegisterRequest_WithEmptyPassword_ShouldFailValidation() {
		RegisterRequest request = new RegisterRequest(
						"John",
						"Doe",
						"johndoe@example.com",
						""
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsSequence("password", "password");
	}

	@Test
	@DisplayName("Short password (7 chars) should fail @Size(min = 8)")
	void RegisterRequest_WithShortPassword_ShouldFailValidation() {
		RegisterRequest request = new RegisterRequest(
						"John",
						"Doe",
						"johndoe@example.com",
						"Short1@"
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("password");
	}

	@Test
	@DisplayName("Long password (17 chars) should fail @Size(max = 16)")
	void RegisterRequest_WithLongPassword_ShouldFailValidation() {
		RegisterRequest request = new RegisterRequest(
						"John",
						"Doe",
						"johndoe@example.com",
						"StrongP@ssword123"  // 17 chars
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactly("password");
	}

	@Test
	@DisplayName("Password at min boundary (8 chars) should pass validation")
	void RegisterRequest_WithPasswordAtMinBoundary_ShouldPassValidation() {
		RegisterRequest request = new RegisterRequest(
						"John",
						"Doe",
						"johndoe@example.com",
						"Str0ng@1"   // exactly 8 chars
		);
		assertThat(validator.validate(request)).isEmpty();
	}

	@Test
	@DisplayName("Password at max boundary (16 chars) should pass validation")
	void RegisterRequest_WithPasswordAtMaxBoundary_ShouldPassValidation() {
		RegisterRequest request = new RegisterRequest(
						"John",
						"Doe",
						"johndoe@example.com",
						"StrongP@ssword12"   // exactly 16 chars
		);
		assertThat(validator.validate(request)).isEmpty();
	}

	// MULTIPLE VIOLATIONS
	@Test
	@DisplayName("All null fields should produce violations on all four fields")
	void RegisterRequest_WithAllNullFields_ShouldFailValidationOnAllFields() {
		RegisterRequest request = new RegisterRequest(null, null, null, null);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactlyInAnyOrder("firstName", "lastName", "email", "password");
	}

	@Test
	@DisplayName("Invalid email and short password should produce two violations")
	void RegisterRequest_WithInvalidEmailAndShortPassword_ShouldFailValidationOnBothFields() {
		RegisterRequest request = new RegisterRequest(
						"John",
						"Doe",
						"not-an-email",
						"short"
		);
		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
		assertThat(violations)
						.extracting(v -> v.getPropertyPath().toString())
						.containsExactlyInAnyOrder("email", "password");
	}
}