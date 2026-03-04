package com.ahmedhassan.getthebook.services;

import com.ahmedhassan.getthebook.dtos.requests.RegisterRequest;
import com.ahmedhassan.getthebook.dtos.responses.RegisterResponse;
import com.ahmedhassan.getthebook.entities.User;
import com.ahmedhassan.getthebook.exceptions.RoleNotFoundException;
import com.ahmedhassan.getthebook.mappers.UserMapper;
import com.ahmedhassan.getthebook.repositories.RoleRepository;
import com.ahmedhassan.getthebook.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.ahmedhassan.getthebook.enums.UserRoles.USER;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;

	public RegisterResponse register(
					@NonNull RegisterRequest registerRequest
	) throws RoleNotFoundException {
		var userRole = roleRepository.findRoleByName(USER.name())
						.orElseThrow(() ->
										new RoleNotFoundException(USER.name() + " role not found")); // TODO: Exception
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
		var saveUser = this.userRepository.save(rawUser);
		return UserMapper.userEntityToRegisterResponse(saveUser);
	}
}