package com.ahmedhassan.getthebook.controllers;

import com.ahmedhassan.getthebook.annotations.openapi.atomic.ApiConflictResponse;
import com.ahmedhassan.getthebook.annotations.openapi.atomic.ApiNotFoundResponse;
import com.ahmedhassan.getthebook.annotations.openapi.atomic.ApiUnauthorizedResponse;
import com.ahmedhassan.getthebook.annotations.openapi.composed.ApiCommonResponse;
import com.ahmedhassan.getthebook.dtos.requests.LoginRequest;
import com.ahmedhassan.getthebook.dtos.requests.RegisterRequest;
import com.ahmedhassan.getthebook.dtos.responses.LoginResponse;
import com.ahmedhassan.getthebook.dtos.responses.RegisterResponse;
import com.ahmedhassan.getthebook.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static com.ahmedhassan.getthebook.utils.Utils.maskEmail;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Manage user authentication")
public class AuthController {

	private final AuthService _authService;

	@SecurityRequirement(name = "")
	@PostMapping("/register")
	@Operation(
					summary = "Register a new user",
					description = "Creates a new user account in the system",
					requestBody =  @io.swagger.v3.oas.annotations.parameters.RequestBody(
									description = "User registration details",
									required = true
					)
	)
	@ApiResponse(
					responseCode = "201",
					description = "User registered successfully",
					content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = RegisterResponse.class)

					))
	@ApiConflictResponse
	@ApiCommonResponse
	public ResponseEntity<RegisterResponse> register(
					@RequestBody
					@Valid
					RegisterRequest registerRequest
	) {
		log.info("Create user request received for email={}", maskEmail(registerRequest.email()));

		var response = _authService.register(registerRequest);

		log.info("Create user request processed successfully for email={}", maskEmail(registerRequest.email()));

		return ResponseEntity
						.status(HttpStatus.CREATED)
						.location(URI.create("/users/" + response.id()))
						.body(response);
	}

	@SecurityRequirement(name = "")
	@PostMapping("/login")
	@Operation(
					summary = "Login a user",
					description = "Login an existing user in system to account",
					requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
									description = "User login details",
									required = true
					)
	)
	@ApiResponse(
					responseCode = "200",
					description = "User logged in successfully",
					content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = LoginResponse.class)
					)
	)
	@ApiUnauthorizedResponse
	@ApiNotFoundResponse
	@ApiCommonResponse
	public ResponseEntity<LoginResponse> login(
					@RequestBody
					@Valid
					LoginRequest loginRequest
	) {
		log.info("Login request received for email={}", maskEmail(loginRequest.email()));

		var response = _authService.login(loginRequest);
		log.info("Login request processed successfully for email={}", maskEmail(loginRequest.email()));
		return ResponseEntity
						.status(HttpStatus.OK)
						.body(response);
	}
}