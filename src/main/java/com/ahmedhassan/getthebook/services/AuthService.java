package com.ahmedhassan.getthebook.services;

import com.ahmedhassan.getthebook.dtos.requests.LoginRequest;
import com.ahmedhassan.getthebook.dtos.requests.RegisterRequest;
import com.ahmedhassan.getthebook.dtos.responses.LoginResponse;
import com.ahmedhassan.getthebook.dtos.responses.RegisterResponse;
import com.ahmedhassan.getthebook.entities.User;
import com.ahmedhassan.getthebook.exceptions.RoleNotFoundException;
import com.ahmedhassan.getthebook.repositories.RoleRepository;
import com.ahmedhassan.getthebook.repositories.UserRepository;
import com.ahmedhassan.getthebook.security.services.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;

import static com.ahmedhassan.getthebook.enums.UserRoles.USER;
import static com.ahmedhassan.getthebook.mappers.UserMapper.toRegisterResponse;
import static com.ahmedhassan.getthebook.mappers.UserMapper.toLoginResponse;
import static com.ahmedhassan.getthebook.utils.Utils.maskEmail;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
	private final PasswordEncoder _passwordEncoder;
	private final UserRepository _userRepository;
	private final RoleRepository _roleRepository;
	private final AuthenticationManager _authenticationManager;
	private final JwtService _jwtService;

	public RegisterResponse register(
					@NonNull RegisterRequest registerRequest
	) {
		log.info("Initializing user service to register user email: {}", maskEmail(registerRequest.email()));
		var userRole = _roleRepository.findRoleByName(USER.name())
						.orElseThrow(() -> {
							log.warn("Registration failed. No role found with name {}", USER.name());
							return new RoleNotFoundException(USER.name() + " role not found");
						});
		log.info("Compiling user information to save in database");
		var rawUser = User
						.builder()
						.firstName(registerRequest.firstName())
						.lastName(registerRequest.lastName())
						.email(registerRequest.email())
						.password(_passwordEncoder.encode(registerRequest.password()))
						.isAccountEnabled(true) // TODO: Implement email validation
						.isAccountLocked(false)
						.role(userRole)
						.build();
		log.info("User information compiled. Saving user information to database");
		var saveUser = _userRepository.save(rawUser);
		log.info("User registered successfully email={}", maskEmail(registerRequest.email()));
		return toRegisterResponse(saveUser);
	}

	public LoginResponse login(
					@NonNull LoginRequest loginRequest
	) {
		log.info("Initializing services to login user email: {}", maskEmail(loginRequest.email()));
		var auth = _authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(
										loginRequest.email(),
										loginRequest.password()
						)
		);

		log.info("User successfully authenticated");
		log.info("Generating JWT token for authenticated user...");
		if (!(auth.getPrincipal() instanceof User user)) {
			throw new IllegalStateException(
							"Unexpected principal type: " + Objects.requireNonNull(auth.getPrincipal()).getClass().getName()
			);
		}
		var claims = new HashMap<String, Object>();
		claims.put("fullName", user.getFullName());
		claims.put("id", user.getId());
		var jwtToken = _jwtService.generateJwtToken(claims, user);
		log.info("Generated JWT token for authenticated user");
		return toLoginResponse(user, jwtToken);
	}
}