package com.ahmedhassan.getthebook.services;

import com.ahmedhassan.getthebook.dtos.requests.RegisterRequest;
import com.ahmedhassan.getthebook.dtos.responses.RegisterResponse;
import com.ahmedhassan.getthebook.entities.User;
import com.ahmedhassan.getthebook.exceptions.RoleNotFoundException;
import com.ahmedhassan.getthebook.mappers.UserMapper;
import com.ahmedhassan.getthebook.repositories.RoleRepository;
import com.ahmedhassan.getthebook.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.ahmedhassan.getthebook.enums.UserRoles.USER;
import static com.ahmedhassan.getthebook.utils.Utils.maskEmail;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;

	public RegisterResponse register(
					@NonNull RegisterRequest registerRequest
	) throws RoleNotFoundException {
		log.info("Initializing user service to register user email: {}", maskEmail(registerRequest.email()));
		var userRole = roleRepository.findRoleByName(USER.name())
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
						.password(passwordEncoder.encode(registerRequest.password()))
						.isAccountEnabled(true) // TODO: Implement email validation
						.isAccountLocked(false)
						.role(userRole)
						.build();
		log.info("User information compiled. Saving user information to database");
		var saveUser = this.userRepository.save(rawUser);
		log.info("User registered successfully email={}", maskEmail(registerRequest.email()));
		return UserMapper.userEntityToRegisterResponse(saveUser);
	}
}