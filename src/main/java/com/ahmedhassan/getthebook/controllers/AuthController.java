package com.ahmedhassan.getthebook.controllers;

import com.ahmedhassan.getthebook.dtos.requests.RegisterRequest;
import com.ahmedhassan.getthebook.dtos.responses.RegisterResponse;
import com.ahmedhassan.getthebook.entities.User;
import com.ahmedhassan.getthebook.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;
	@PostMapping("/register")
	public ResponseEntity<RegisterResponse> register(
					@RequestBody @Valid RegisterRequest registerRequest
	) {
		log.info("Create user request received for email={}", registerRequest.email());
		var response = authService.register(registerRequest);
		log.info("Create user request processed successfully for email={}", registerRequest.email());
		// TODO: Add location headers in response
		return ResponseEntity
						.status(HttpStatus.CREATED)
						.body(response);
	}
}