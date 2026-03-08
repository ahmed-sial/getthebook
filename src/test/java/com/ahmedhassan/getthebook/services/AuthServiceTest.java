package com.ahmedhassan.getthebook.services;

import com.ahmedhassan.getthebook.entities.Role;
import com.ahmedhassan.getthebook.entities.User;
import com.ahmedhassan.getthebook.repositories.RoleRepository;
import com.ahmedhassan.getthebook.repositories.UserRepository;
import com.ahmedhassan.getthebook.security.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.ahmedhassan.getthebook.common.TestDataBuilder.buildRole;
import static com.ahmedhassan.getthebook.common.TestDataBuilder.buildUser;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
public class AuthServiceTest {

	@Mock private UserRepository _userRepository;
	@Mock private PasswordEncoder _passwordEncoder;
	@Mock private RoleRepository _roleRepository;
	@Mock AuthenticationManager _authenticationManager;
	@Mock JwtService _jwtService;

	@InjectMocks private AuthService _authService;

	private Role testRole;
	private User testUser;

	@BeforeEach
	void setUp() {
		testRole = buildRole("USER");
		testUser = buildUser(testRole);
	}

	// register
	@Nested
	@DisplayName("register()")
	class RegisterTests {
		@Test
		void register_WithValidRequestResponse_ShouldRegisterNewUser() {

		}
	}

}