package com.ahmedhassan.getthebook.controllers;

import com.ahmedhassan.getthebook.configurations.AppBeansConfigTest;
import com.ahmedhassan.getthebook.dtos.requests.LoginRequest;
import com.ahmedhassan.getthebook.dtos.requests.RegisterRequest;
import com.ahmedhassan.getthebook.security.filters.JwtAuthFilter;
import com.ahmedhassan.getthebook.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.ahmedhassan.getthebook.common.TestDataBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
				controllers = AuthController.class,
				excludeFilters = @ComponentScan.Filter(
								type = FilterType.ASSIGNABLE_TYPE,
								classes = {
												JwtAuthFilter.class
												// add other filter to exclude here
								}
				)
)
@Import({AppBeansConfigTest.class})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("AuthController Web Layer Tests")
public class AuthControllerTest {
	@Autowired
	protected MockMvc _mockMvc;
	@Autowired
	protected ObjectMapper _objectMapper;
	@MockitoBean
	private AuthService _authService;

	// POST /auth/register
	@Nested
	@DisplayName("POST /auth/register")
	class RegisterEndpointTests {

		@Test
		@DisplayName("Should return 201 Created with body for valid registration")
		void register_WithValidBody_ShouldReturn201Created() throws Exception {
			var request = buildRegisterRequest();
			var response = buildRegisterResponse();

			given(_authService.register(any())).willReturn(response);

			_mockMvc.perform(post("/auth/register")
											.contentType(MediaType.APPLICATION_JSON)
											.content(_objectMapper.writeValueAsString(request)))
							.andExpect(status().isCreated())
							.andExpect(jsonPath("$.email").value(response.email()))
							.andExpect(jsonPath("$.firstName").value(response.firstName()));
		}

		@Test
		@DisplayName("Should return Location header pointing to created user resource")
		void register_WithValidBody_ShouldReturnLocationHeader() throws Exception {
			var request = buildRegisterRequest();
			var response = buildRegisterResponse();   // assumes response.id() == "user-123"

			given(_authService.register(any())).willReturn(response);

			_mockMvc.perform(post("/auth/register")
											.contentType(MediaType.APPLICATION_JSON)
											.content(_objectMapper.writeValueAsString(request)))
							.andExpect(status().isCreated())
							.andExpect(header().string("Location", "/users/" + response.id()));
		}

		@Test
		@DisplayName("Should delegate request fields to AuthService")
		void register_WithValidBody_ShouldDelegateToService() throws Exception {
			var request = buildRegisterRequest();
			var response = buildRegisterResponse();

			given(_authService.register(any())).willReturn(response);

			_mockMvc.perform(post("/auth/register")
											.contentType(MediaType.APPLICATION_JSON)
											.content(_objectMapper.writeValueAsString(request)))
							.andExpect(status().isCreated());

			var captor = ArgumentCaptor.forClass(RegisterRequest.class);
			then(_authService).should(times(1)).register(captor.capture());
			assertThat(captor.getValue().email()).isEqualTo(request.email());
			assertThat(captor.getValue().firstName()).isEqualTo(request.firstName());
		}

		@Test
		@DisplayName("Should return 400 Bad Request for invalid request body")
		void register_WithInvalidBody_ShouldReturn400BadRequest() throws Exception {
			var request = buildInvalidRegisterRequest();

			_mockMvc.perform(post("/auth/register")
											.contentType(MediaType.APPLICATION_JSON)
											.content(_objectMapper.writeValueAsString(request)))
							.andExpect(status().isBadRequest())
							.andExpect(jsonPath("$.error").value("Bad Request"));
		}

		@Test
		@DisplayName("Should return 400 Bad Request when body is empty")
		void register_WithEmptyBody_ShouldReturn400BadRequest() throws Exception {
			_mockMvc.perform(post("/auth/register")
											.contentType(MediaType.APPLICATION_JSON)
											.content("{}"))
							.andExpect(status().isBadRequest())
							.andExpect(jsonPath("$.error").value("Bad Request"));
		}

		@Test
		@DisplayName("Should return 400 Bad Request when body is missing entirely")
		void register_WithNoBody_ShouldReturn400BadRequest() throws Exception {
			_mockMvc.perform(post("/auth/register")
											.contentType(MediaType.APPLICATION_JSON))
							.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Should not call AuthService when request validation fails")
		void register_WithInvalidBody_ShouldNeverCallService() throws Exception {
			_mockMvc.perform(post("/auth/register")
											.contentType(MediaType.APPLICATION_JSON)
											.content(_objectMapper.writeValueAsString(buildInvalidRegisterRequest())))
							.andExpect(status().isBadRequest());

			then(_authService).shouldHaveNoInteractions();
		}

		@Test
		@DisplayName("Should return 409 Conflict when email is already registered")
		void register_WithDuplicateEmail_ShouldReturn409Conflict() throws Exception {
			var request = buildRegisterRequest();

			given(_authService.register(any()))
							.willThrow(new DataIntegrityViolationException("Email already in use"));

			_mockMvc.perform(post("/auth/register")
											.contentType(MediaType.APPLICATION_JSON)
											.content(_objectMapper.writeValueAsString(request)))
							.andExpect(status().isConflict())
							.andExpect(jsonPath("$.error").value("Conflict"));
		}

		@Test
		@DisplayName("Should return 415 Unsupported Media Type for non-JSON content type")
		void register_WithWrongContentType_ShouldReturn415() throws Exception {
			var request = buildRegisterRequest();

			_mockMvc.perform(post("/auth/register")
											.contentType(MediaType.TEXT_PLAIN)
											.content(_objectMapper.writeValueAsString(request)))
							.andExpect(status().isUnsupportedMediaType());
		}

		@Test
		@DisplayName("Should return 500 Internal Server Error on unexpected service failure")
		void register_WhenServiceThrowsUnexpectedException_ShouldReturn500() throws Exception {
			var request = buildRegisterRequest();

			given(_authService.register(any()))
							.willThrow(new RuntimeException("Unexpected failure"));

			_mockMvc.perform(post("/auth/register")
											.contentType(MediaType.APPLICATION_JSON)
											.content(_objectMapper.writeValueAsString(request)))
							.andExpect(status().isInternalServerError());
		}
	}

	@Nested
	@DisplayName("POST /auth/login")
	class LoginEndpointTests {

		@Test
		@DisplayName("Should return 200 OK with response body for valid credentials")
		void login_WithValidCredentials_ShouldReturn200Ok() throws Exception {
			var request  = buildLoginRequest();
			var response = buildLoginResponse();

			given(_authService.login(any())).willReturn(response);

			_mockMvc.perform(post("/auth/login")
											.contentType(MediaType.APPLICATION_JSON)
											.content(_objectMapper.writeValueAsString(request)))
							.andExpect(status().isOk())
							.andExpect(jsonPath("$.token").value(response.token()));
		}

		@Test
		@DisplayName("Should delegate correct credentials to AuthService")
		void login_WithValidCredentials_ShouldDelegateToService() throws Exception {
			var request  = buildLoginRequest();
			var response = buildLoginResponse();

			given(_authService.login(any())).willReturn(response);

			_mockMvc.perform(post("/auth/login")
											.contentType(MediaType.APPLICATION_JSON)
											.content(_objectMapper.writeValueAsString(request)))
							.andExpect(status().isOk());

			var captor = ArgumentCaptor.forClass(LoginRequest.class);
			then(_authService).should(times(1)).login(captor.capture());
			assertThat(captor.getValue().email()).isEqualTo(request.email());
			assertThat(captor.getValue().password()).isEqualTo(request.password());
		}

		@Test
		@DisplayName("Should return 400 Bad Request for invalid request body fields")
		void login_WithInvalidBody_ShouldReturn400BadRequest() throws Exception {
			var request = buildInvalidLoginRequest();   // blank email, blank password

			_mockMvc.perform(post("/auth/login")
											.contentType(MediaType.APPLICATION_JSON)
											.content(_objectMapper.writeValueAsString(request)))
							.andExpect(status().isBadRequest())
							.andExpect(jsonPath("$.error").value("Bad Request"));
		}

		@Test
		@DisplayName("Should return 400 Bad Request when body is empty JSON object")
		void login_WithEmptyBody_ShouldReturn400BadRequest() throws Exception {
			_mockMvc.perform(post("/auth/login")
											.contentType(MediaType.APPLICATION_JSON)
											.content("{}"))
							.andExpect(status().isBadRequest())
							.andExpect(jsonPath("$.error").value("Bad Request"));
		}

		@Test
		@DisplayName("Should return 400 Bad Request when body is missing entirely")
		void login_WithNoBody_ShouldReturn400BadRequest() throws Exception {
			_mockMvc.perform(post("/auth/login")
											.contentType(MediaType.APPLICATION_JSON))
							.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Should not call AuthService when request validation fails")
		void login_WithInvalidBody_ShouldNeverCallService() throws Exception {
			_mockMvc.perform(post("/auth/login")
											.contentType(MediaType.APPLICATION_JSON)
											.content(_objectMapper.writeValueAsString(buildInvalidRegisterRequest())))
							.andExpect(status().isBadRequest());

			then(_authService).shouldHaveNoInteractions();
		}

		@Test
		@DisplayName("Should return 401 Unauthorized when credentials are invalid")
		void login_WithInvalidCredentials_ShouldReturn401Unauthorized() throws Exception {
			var request = buildLoginRequest();

			given(_authService.login(any()))
							.willThrow(new BadCredentialsException("Invalid email or password"));

			_mockMvc.perform(post("/auth/login")
											.contentType(MediaType.APPLICATION_JSON)
											.content(_objectMapper.writeValueAsString(request)))
							.andExpect(status().isUnauthorized())
							.andExpect(jsonPath("$.error").value("Unauthorized"));
		}

		@Test
		@DisplayName("Should return 415 Unsupported Media Type for non-JSON content type")
		void login_WithWrongContentType_ShouldReturn415() throws Exception {
			_mockMvc.perform(post("/auth/login")
											.contentType(MediaType.TEXT_PLAIN)
											.content("email=john@example.com&password=secret"))
							.andExpect(status().isUnsupportedMediaType());
		}

		@Test
		@DisplayName("Should return 500 Internal Server Error on unexpected service failure")
		void login_WhenServiceThrowsUnexpectedException_ShouldReturn500() throws Exception {
			var request = buildLoginRequest();

			given(_authService.login(any()))
							.willThrow(new RuntimeException("Unexpected failure"));

			_mockMvc.perform(post("/auth/login")
											.contentType(MediaType.APPLICATION_JSON)
											.content(_objectMapper.writeValueAsString(request)))
							.andExpect(status().isInternalServerError());
		}
	}
}

// HOW TO FIND THE NUMBER OF TEST CASES REQUIRED FOR VALIDATION TESTING, REPO TESTING, SERVICE/BUSINESS TESTING, WEB LAYER TESTING.
// SHOULD DO TESTING OF GLOBAL EXCEPTION HANDLER?