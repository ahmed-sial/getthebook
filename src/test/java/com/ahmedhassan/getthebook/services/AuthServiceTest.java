package com.ahmedhassan.getthebook.services;

import com.ahmedhassan.getthebook.dtos.requests.LoginRequest;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static com.ahmedhassan.getthebook.common.TestDataBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
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
	// Add other relationships here

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
		// CHECK: Should check auditing fields?
		@Test
		@DisplayName("Should encode password before saving - never store plain text")
		void register_WithValidRequest_ShouldEncodePasswordBeforeSaving() {
			// Arrange
			given(_roleRepository.findRoleByName("USER")).willReturn(Optional.of(testRole));
			given(_passwordEncoder.encode(anyString())).willReturn("encodedPassword");
			given(_userRepository.save(any(User.class))).willReturn(testUser);

			_authService.register(validRequest);

			var userCaptor = ArgumentCaptor.forClass(User.class);
			then(_userRepository).should(times(1))
							.save(userCaptor.capture());

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

		private LoginRequest validRequest;

		@BeforeEach
		void setUp() {
			validRequest = buildLoginRequest();
		}

		@Test
		@DisplayName("Should return response with JWT token when credentials are valid")
		void login_WithValidRequest_ShouldReturnLoginResponseWithJwtToken() {
			var authToken = new UsernamePasswordAuthenticationToken(
							testUser,
							null,
							testUser.getAuthorities()
			);
			given(_authenticationManager.authenticate(any())).willReturn(authToken);
			given(_jwtService.generateJwtToken(any(), any(User.class)))
							.willReturn("jwt-token-xyz");

			var response = _authService.login(validRequest);

			assertThat(response).isNotNull();
			assertThat(response.token()).isEqualTo("jwt-token-xyz");
			assertThat(response.email()).isEqualTo(testUser.getEmail());
			// CHECK: Should all properties be checked or only one or two?
		}

		@Test
		@DisplayName("Should return JWT token exactly once on successful login")
		void login_WithValidRequest_ShouldReturnJWTTokenExactlyOnce() {
			var authToken = new UsernamePasswordAuthenticationToken(
							testUser,
							null,
							testUser.getAuthorities()
			);

			given(_authenticationManager.authenticate(any())).willReturn(authToken);
			given(_jwtService.generateJwtToken(any(), any(User.class)))
							.willReturn("jwt-token-xyz");

			_authService.login(validRequest);
			then(_jwtService).should(times(1))
							.generateJwtToken(any(), any(User.class));
		}

		@Test
		@DisplayName("Should include fullName and id in JWT claims")
		void login_WithValidRequest_ShouldIncludeFullNameAndIdInJWTClaims() {
			var authToken = new UsernamePasswordAuthenticationToken(
							testUser,
							null,
							testUser.getAuthorities()
			);
			given(_authenticationManager.authenticate(any())).willReturn(authToken);
			given(_jwtService.generateJwtToken(any(), any(User.class)))
							.willReturn("jwt-token-xyz");

			_authService.login(validRequest);

			var claimsCaptor = ArgumentCaptor.forClass(HashMap.class);
			then(_jwtService).should(times(1))
							.generateJwtToken(claimsCaptor.capture(), any(User.class));

			var capturedClaims = claimsCaptor.getValue();
			assertThat(capturedClaims).containsKey("fullName");
			assertThat(capturedClaims).containsKey("id");
		}

		@Test
		@DisplayName("Should throw when credentials are wrong")
		void login_WithWrongCredentials_ShouldThrowBadCredentialsException() {
			given(_authenticationManager.authenticate(any()))
							.willThrow(new BadCredentialsException("Bad credentials"));

			assertThatThrownBy(() -> _authService.login(validRequest))
							.isInstanceOf(BadCredentialsException.class);

			then(_jwtService).shouldHaveNoInteractions();
		}

		@Test
		@DisplayName("Should throw IllegalStateException when principal is not a User instance")
		void login_WhenPrincipalIsNotUser_ShouldThrowIllegalStateException() {
			UsernamePasswordAuthenticationToken authToken =
							new UsernamePasswordAuthenticationToken("just-a-string", null);
			given(_authenticationManager.authenticate(any())).willReturn(authToken);

			assertThatThrownBy(() -> _authService.login(validRequest))
							.isInstanceOf(IllegalStateException.class)
							.hasMessageContaining("Unexpected principal type");
		}

	}

}