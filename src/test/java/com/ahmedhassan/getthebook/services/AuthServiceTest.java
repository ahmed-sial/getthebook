package com.ahmedhassan.getthebook.services;

import com.ahmedhassan.getthebook.dtos.requests.RegisterRequest;
import com.ahmedhassan.getthebook.entities.Role;
import com.ahmedhassan.getthebook.entities.User;
import com.ahmedhassan.getthebook.exceptions.RoleNotFoundException;
import com.ahmedhassan.getthebook.repositories.RoleRepository;
import com.ahmedhassan.getthebook.repositories.UserRepository;
import com.ahmedhassan.getthebook.security.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static com.ahmedhassan.getthebook.common.TestDataBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

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
		testUser.setId(UUID.randomUUID());
	}

	// register
	@Nested
	@DisplayName("register()")
	class RegisterTests {

		private RegisterRequest validRequest;

		@BeforeEach
		void setUp() {
			validRequest = buildRegisterRequest();
		}

		@Test
		@DisplayName("Should return response with correct user details when request is valid")
		void register_WithValidRequestResponse_ShouldReturnCorrectResponse() {
			// ARRANGE -> what to return
			given(_roleRepository.findRoleByName("USER")).willReturn(Optional.of(testRole));
			given(_passwordEncoder.encode(anyString())).willReturn("encodedPassword");
			given(_userRepository.save(any(User.class))).willReturn(testUser);

			// ACT
			var response = _authService.register(validRequest);

			// ASSERT
			assertThat(response).isNotNull();
			assertThat(response.email()).isEqualTo(testUser.getEmail());
			assertThat(response.firstName()).isEqualTo(testUser.getFirstName());
			assertThat(response.lastName()).isEqualTo(testUser.getLastName());
		}

		@Test
		@DisplayName("Should encode password before saving - never store plain text")
		void register_WithValidRequest_ShouldEncodePasswordBeforeSaving() {
			// Arrange
			given(_roleRepository.findRoleByName("USER")).willReturn(Optional.of(testRole));
			given(_passwordEncoder.encode(anyString())).willReturn("encodedPassword");
			given(_userRepository.save(any(User.class))).willReturn(testUser);

			_authService.register(validRequest);

			ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
			then(_userRepository).should(times(1)).save(userCaptor.capture());

			User savedUser = userCaptor.getValue();
			assertThat(savedUser.getPassword())
							.isEqualTo("encodedPassword")
							.isNotEqualTo("StrongP@ssword");
		}

		@Test
		@DisplayName("Should assign USER role to the registered user")
		void register_WithValidRequest_ShouldAssignUserRole() {
			given(_roleRepository.findRoleByName("USER")).willReturn(Optional.of(testRole));
			given(_passwordEncoder.encode(anyString())).willReturn("encodedPassword");
			given(_userRepository.save(any(User.class))).willReturn(testUser);

			_authService.register(validRequest);

			ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
			then(_userRepository).should().save(userCaptor.capture());

			assertThat(userCaptor.getValue().getRole().getName()).isEqualTo("USER");
		}

		@Test
		@DisplayName("Should save user exactly once to database")
		void register_WithValidRequest_ShouldSaveUserOnce() {
			given(_roleRepository.findRoleByName("USER")).willReturn(Optional.of(testRole));
			given(_passwordEncoder.encode(anyString())).willReturn("encodedPassword");
			given(_userRepository.save(any(User.class))).willReturn(testUser);

			_authService.register(validRequest);

			then(_userRepository).should(times(1)).save(any(User.class));
		}

		@Test
		@DisplayName("Should throw RoleNotFoundException when USER role doesn't exist")
		void register_WithRoleNotFound_ShouldThrowRoleNotFoundException() {
			given(_roleRepository.findRoleByName("USER")).willReturn(Optional.empty());

			assertThatThrownBy(() -> _authService.register(validRequest))
							.isInstanceOf(RoleNotFoundException.class)
							.hasMessageContaining("USER");

		}

		@Test
		@DisplayName("Should set account as enabled and unlocked on registration")
		void register_WithValidRequest_ShouldSetAccountAsEnabledAndUnlockedOnRegistration() {
			given(_roleRepository.findRoleByName("USER")).willReturn(Optional.of(testRole));
			given(_passwordEncoder.encode(anyString())).willReturn("encodedPassword");
			given(_userRepository.save(any(User.class))).willReturn(testUser);

			_authService.register(validRequest);
			ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
			then(_userRepository).should().save(userCaptor.capture());

			var user = userCaptor.getValue();
			assertThat(user.getIsAccountEnabled()).isTrue();
			assertThat(user.getIsAccountLocked()).isFalse();
		}
	}

	// login()
	@Nested
	@DisplayName("login()")
	class LoginTests {
	}

}