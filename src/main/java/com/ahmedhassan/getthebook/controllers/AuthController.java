package com.ahmedhassan.getthebook.controllers;

import com.ahmedhassan.getthebook.dtos.requests.RegisterRequest;
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

	private final AuthService authService;

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
	public ResponseEntity<RegisterResponse> register(
					@RequestBody
					@Valid
					RegisterRequest registerRequest
	) {
		log.info("Create user request received for email={}", maskEmail(registerRequest.email()));

		var response = authService.register(registerRequest);

		log.info("Create user request processed successfully for email={}", maskEmail(registerRequest.email()));

		return ResponseEntity
						.status(HttpStatus.CREATED)
						.location(URI.create("/users/" + response.id()))
						.body(response);
	}
}