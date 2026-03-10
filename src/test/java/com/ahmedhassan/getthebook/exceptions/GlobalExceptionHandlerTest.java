package com.ahmedhassan.getthebook.exceptions;

import com.ahmedhassan.getthebook.configurations.AppBeansConfigTest;
import com.ahmedhassan.getthebook.security.filters.JwtAuthFilter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintDeclarationException;
import jakarta.validation.ConstraintDefinitionException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO: Test it...
@WebMvcTest(
				controllers = GlobalExceptionHandlerTest.FakeController.class,
				excludeFilters = @ComponentScan.Filter(
								type = FilterType.ASSIGNABLE_TYPE,
								classes = {
												JwtAuthFilter.class
								}
				)
)
@Import({AppBeansConfigTest.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class GlobalExceptionHandlerTest {

	// ── Sanitized messages returned by the handler (must match exactly) ───────
	private static final String SANITIZED_SERVER_ERROR =
					"An unexpected error occurred on our side. Please try again later";
	private static final String SANITIZED_DATA_CONFLICT =
					"A data conflict occurred. Please check your input";
	private static final String SANITIZED_NOT_READABLE =
					"Unable to process request. Kindly provide a valid request";

	@Autowired MockMvc _mockMvc;

	// ── Fake controller used to trigger every exception path ─────────────────

	@RestController
	@RequestMapping("/test")
	static class FakeController {

		record FakeRequest(@NotBlank String name, @Email String email) {}

		@GetMapping("/entity-not-found")
		public void throwEntityNotFound() {
			throw new EntityNotFoundException("Resource not found");
		}

		@GetMapping("/bad-credentials")
		public void throwBadCredentials() {
			throw new BadCredentialsException("Bad credentials");
		}

		@GetMapping("/constraint-violation")
		public void throwConstraintViolation() {
			throw new ConstraintViolationException("Constraint violated", Collections.emptySet());
		}

		@GetMapping("/constraint-declaration")
		public void throwConstraintDeclaration() {
			throw new ConstraintDeclarationException("Constraint declaration error");
		}

		@GetMapping("/constraint-definition")
		public void throwConstraintDefinition() {
			throw new ConstraintDefinitionException("Constraint definition error");
		}

		@GetMapping("/data-integrity-violation")
		public void throwDataIntegrityViolation() {
			throw new DataIntegrityViolationException("Unique constraint violated");
		}

		@GetMapping("/access-denied")
		public void throwAccessDenied() {
			throw new AccessDeniedException("Access denied");
		}

		@GetMapping("/authentication")
		public void throwAuthentication() {
			throw new InsufficientAuthenticationException("Authentication required");
		}

		@GetMapping("/illegal-argument")
		public void throwIllegalArgument() {
			throw new IllegalArgumentException("Illegal argument");
		}

		@GetMapping("/illegal-state")
		public void throwIllegalState() {
			throw new IllegalStateException("Illegal state");
		}

		@GetMapping("/max-upload-size")
		public void throwMaxUploadSize() {
			throw new MaxUploadSizeExceededException(1024L);
		}

		@GetMapping("/io-exception")
		public void throwIOException() throws IOException {
			throw new IOException("IO failure");
		}

		@GetMapping("/generic-exception")
		public void throwGenericException() throws Exception {
			throw new Exception("Unexpected error");
		}

		// ── Triggered naturally by Spring ──────────────────────────────────────

		// HttpMessageNotReadableException → send malformed/missing JSON body
		@PostMapping("/message-not-readable")
		public void triggerMessageNotReadable(@RequestBody String body) {}

		// MethodArgumentNotValidException → send invalid body against @Valid
		@PostMapping("/method-arg-not-valid")
		public void triggerMethodArgNotValid(@RequestBody @Valid FakeRequest body) {}

		// BindException → send invalid form fields
		@PostMapping("/bind-exception")
		public void triggerBindException(@Valid FakeRequest body) {}

		// MissingServletRequestParameterException → omit required param
		@GetMapping("/missing-param")
		public void triggerMissingParam(@RequestParam String requiredParam) {}

		// MethodArgumentTypeMismatchException → send "abc" for a Long
		@GetMapping("/type-mismatch/{id}")
		public void triggerTypeMismatch(@PathVariable Long id) {}

		// HttpRequestMethodNotSupportedException → call with wrong HTTP method
		@GetMapping("/method-not-allowed")
		public void triggerMethodNotAllowed() {}

		// HttpMediaTypeNotSupportedException → send wrong Content-Type
		@PostMapping(value = "/unsupported-media-type", consumes = MediaType.APPLICATION_JSON_VALUE)
		public void triggerUnsupportedMediaType(@RequestBody String body) {}

		// HttpMediaTypeNotAcceptableException → send Accept: text/plain
		@GetMapping(value = "/not-acceptable", produces = MediaType.APPLICATION_JSON_VALUE)
		public String triggerNotAcceptable() {
			return "response";
		}

		// HttpMessageNotWritableException → non-serializable return type
		@GetMapping("/message-not-writable")
		public Object triggerMessageNotWritable() {
			return new Object() {
				public final Object circular = this; // causes Jackson serialization failure
			};
		}
	}

	// ── 404 ───────────────────────────────────────────────────────────────────

	@Test
	@DisplayName("EntityNotFoundException → 404 with correct error shape")
	void whenEntityNotFound_ShouldReturn404() throws Exception {
		_mockMvc.perform(get("/test/entity-not-found"))
						.andExpect(status().isNotFound())
						.andExpect(jsonPath("$.status").value(404))
						.andExpect(jsonPath("$.error").value("Not Found"))
						.andExpect(jsonPath("$.message").value("Resource not found"))
						.andExpect(jsonPath("$.path").value("/test/entity-not-found"));
	}

	@Test
	@DisplayName("NoHandlerFoundException / unmapped path → 404")
	void whenNoHandlerFound_ShouldReturn404() throws Exception {
		// Spring Boot 3.x raises NoResourceFoundException for unknown paths;
		// both NoHandlerFoundException and NoResourceFoundException are handled → 404
		_mockMvc.perform(get("/test/unmapped-path"))
						.andExpect(status().isNotFound())
						.andExpect(jsonPath("$.status").value(404))
						.andExpect(jsonPath("$.error").value("Not Found"))
						.andExpect(jsonPath("$.path").value("/test/unmapped-path"));
	}

	// ── 401 ───────────────────────────────────────────────────────────────────

	@Test
	@DisplayName("BadCredentialsException → 401 with correct error shape")
	void whenBadCredentials_ShouldReturn401() throws Exception {
		_mockMvc.perform(get("/test/bad-credentials"))
						.andExpect(status().isUnauthorized())
						.andExpect(jsonPath("$.status").value(401))
						.andExpect(jsonPath("$.error").value("Unauthorized"))
						.andExpect(jsonPath("$.message").value("Bad credentials"))
						.andExpect(jsonPath("$.path").value("/test/bad-credentials"));
	}

	@Test
	@DisplayName("AuthenticationException → 401 with correct error shape")
	void whenAuthenticationException_ShouldReturn401() throws Exception {
		_mockMvc.perform(get("/test/authentication"))
						.andExpect(status().isUnauthorized())
						.andExpect(jsonPath("$.status").value(401))
						.andExpect(jsonPath("$.error").value("Unauthorized"))
						.andExpect(jsonPath("$.message").value("Authentication required"))
						.andExpect(jsonPath("$.path").value("/test/authentication"));
	}

	// ── 403 ───────────────────────────────────────────────────────────────────

	@Test
	@DisplayName("AccessDeniedException → 403 with correct error shape")
	void whenAccessDenied_ShouldReturn403() throws Exception {
		_mockMvc.perform(get("/test/access-denied"))
						.andExpect(status().isForbidden())
						.andExpect(jsonPath("$.status").value(403))
						.andExpect(jsonPath("$.error").value("Forbidden"))
						.andExpect(jsonPath("$.message").value("Access denied"))
						.andExpect(jsonPath("$.path").value("/test/access-denied"));
	}

	// ── 400 ───────────────────────────────────────────────────────────────────

	@Test
	@DisplayName("MethodArgumentNotValidException → 400 with validation details map")
	void whenMethodArgNotValid_ShouldReturn400WithDetails() throws Exception {
		_mockMvc.perform(post("/test/method-arg-not-valid")
										.contentType(MediaType.APPLICATION_JSON)
										.content("{\"name\": \"\", \"email\": \"not-an-email\"}"))
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.status").value(400))
						.andExpect(jsonPath("$.error").value("Bad Request"))
						.andExpect(jsonPath("$.message").value("Validation failed"))
						.andExpect(jsonPath("$.details.name").isNotEmpty())
						.andExpect(jsonPath("$.details.email").isNotEmpty())
						.andExpect(jsonPath("$.path").value("/test/method-arg-not-valid"));
	}

	@Test
	@DisplayName("ConstraintViolationException → 400 with correct error shape")
	void whenConstraintViolation_ShouldReturn400() throws Exception {
		_mockMvc.perform(get("/test/constraint-violation"))
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.status").value(400))
						.andExpect(jsonPath("$.error").value("Bad Request"))
						.andExpect(jsonPath("$.message").value("Constraint violated"))
						.andExpect(jsonPath("$.path").value("/test/constraint-violation"));
	}

	@Test
	@DisplayName("BindException → 400 with correct error shape")
	void whenBindException_ShouldReturn400() throws Exception {
		_mockMvc.perform(post("/test/bind-exception")
										.contentType(MediaType.APPLICATION_FORM_URLENCODED)
										.param("name", "")
										.param("email", "not-an-email"))
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.status").value(400))
						.andExpect(jsonPath("$.error").value("Bad Request"))
						.andExpect(jsonPath("$.path").value("/test/bind-exception"));
	}

	@Test
	@DisplayName("HttpMessageNotReadableException → 400 with sanitized message")
	void whenMessageNotReadable_ShouldReturn400() throws Exception {
		_mockMvc.perform(post("/test/message-not-readable")
										.contentType(MediaType.APPLICATION_JSON)
										.content("{ invalid json }}"))
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.status").value(400))
						.andExpect(jsonPath("$.error").value("Bad Request"))
						.andExpect(jsonPath("$.message").value(SANITIZED_NOT_READABLE))
						.andExpect(jsonPath("$.path").value("/test/message-not-readable"));
	}

	@Test
	@DisplayName("MissingServletRequestParameterException → 400 with correct error shape")
	void whenMissingRequestParam_ShouldReturn400() throws Exception {
		_mockMvc.perform(get("/test/missing-param")) // requiredParam intentionally omitted
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.status").value(400))
						.andExpect(jsonPath("$.error").value("Bad Request"))
						.andExpect(jsonPath("$.message").isNotEmpty())   // Spring builds message automatically
						.andExpect(jsonPath("$.path").value("/test/missing-param"));
	}

	@Test
	@DisplayName("MethodArgumentTypeMismatchException → 400 with correct error shape")
	void whenTypeMismatch_ShouldReturn400() throws Exception {
		_mockMvc.perform(get("/test/type-mismatch/not-a-long"))
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.status").value(400))
						.andExpect(jsonPath("$.error").value("Bad Request"))
						.andExpect(jsonPath("$.message").isNotEmpty())   // Spring builds message automatically
						.andExpect(jsonPath("$.path").value("/test/type-mismatch/not-a-long"));
	}

	@Test
	@DisplayName("IllegalArgumentException → 400 with correct error shape")
	void whenIllegalArgument_ShouldReturn400() throws Exception {
		_mockMvc.perform(get("/test/illegal-argument"))
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.status").value(400))
						.andExpect(jsonPath("$.error").value("Bad Request"))
						.andExpect(jsonPath("$.message").value("Illegal argument"))
						.andExpect(jsonPath("$.path").value("/test/illegal-argument"));
	}

	// ── 405 ───────────────────────────────────────────────────────────────────

	@Test
	@DisplayName("HttpRequestMethodNotSupportedException → 405 with correct error shape")
	void whenMethodNotAllowed_ShouldReturn405() throws Exception {
		_mockMvc.perform(post("/test/method-not-allowed")) // endpoint only supports GET
						.andExpect(status().isMethodNotAllowed())
						.andExpect(jsonPath("$.status").value(405))
						.andExpect(jsonPath("$.error").value("Method Not Allowed"))
						.andExpect(jsonPath("$.message").isNotEmpty())
						.andExpect(jsonPath("$.path").value("/test/method-not-allowed"));
	}

	// ── 406 ───────────────────────────────────────────────────────────────────

	@Test
	@DisplayName("HttpMediaTypeNotAcceptableException → 406 with correct error shape")
	void whenNotAcceptable_ShouldReturn406() throws Exception {
		_mockMvc.perform(get("/test/not-acceptable")
										.accept(MediaType.TEXT_PLAIN)) // endpoint only produces JSON
						.andExpect(status().isNotAcceptable())
						.andExpect(jsonPath("$.status").value(406))
						.andExpect(jsonPath("$.error").value("Not Acceptable"))
						.andExpect(jsonPath("$.path").value("/test/not-acceptable"));
	}

	// ── 409 ───────────────────────────────────────────────────────────────────

	@Test
	@DisplayName("DataIntegrityViolationException → 409 with sanitized message")
	void whenDataIntegrityViolation_ShouldReturn409() throws Exception {
		_mockMvc.perform(get("/test/data-integrity-violation"))
						.andExpect(status().isConflict())
						.andExpect(jsonPath("$.status").value(409))
						.andExpect(jsonPath("$.error").value("Conflict"))
						// handler wraps in a new RuntimeException with this fixed message
						.andExpect(jsonPath("$.message").value(SANITIZED_DATA_CONFLICT))
						.andExpect(jsonPath("$.path").value("/test/data-integrity-violation"));
	}

	@Test
	@DisplayName("IllegalStateException → 409 with correct error shape")
	void whenIllegalState_ShouldReturn409() throws Exception {
		_mockMvc.perform(get("/test/illegal-state"))
						.andExpect(status().isConflict())
						.andExpect(jsonPath("$.status").value(409))
						.andExpect(jsonPath("$.error").value("Conflict"))
						.andExpect(jsonPath("$.message").value("Illegal state"))
						.andExpect(jsonPath("$.path").value("/test/illegal-state"));
	}

	// ── 413 ───────────────────────────────────────────────────────────────────

	@Test
	@DisplayName("MaxUploadSizeExceededException → 413 with correct error shape")
	void whenMaxUploadSizeExceeded_ShouldReturn413() throws Exception {
		_mockMvc.perform(get("/test/max-upload-size"))
						.andExpect(status().isPayloadTooLarge())
						.andExpect(jsonPath("$.status").value(413))
						.andExpect(jsonPath("$.error").value("Payload Too Large"))
						.andExpect(jsonPath("$.message").isNotEmpty())   // message comes from Spring's exception
						.andExpect(jsonPath("$.path").value("/test/max-upload-size"));
	}

	// ── 415 ───────────────────────────────────────────────────────────────────

	@Test
	@DisplayName("HttpMediaTypeNotSupportedException → 415 with correct error shape")
	void whenUnsupportedMediaType_ShouldReturn415() throws Exception {
		_mockMvc.perform(post("/test/unsupported-media-type")
										.contentType(MediaType.TEXT_PLAIN) // endpoint requires JSON
										.content("some body"))
						.andExpect(status().isUnsupportedMediaType())
						.andExpect(jsonPath("$.status").value(415))
						.andExpect(jsonPath("$.error").value("Unsupported Media Type"))
						.andExpect(jsonPath("$.message").isNotEmpty())
						.andExpect(jsonPath("$.path").value("/test/unsupported-media-type"));
	}

	// ── 500 ───────────────────────────────────────────────────────────────────

	@Test
	@DisplayName("ConstraintDeclarationException → 500 with sanitized message")
	void whenConstraintDeclaration_ShouldReturn500() throws Exception {
		_mockMvc.perform(get("/test/constraint-declaration"))
						.andExpect(status().isInternalServerError())
						.andExpect(jsonPath("$.status").value(500))
						.andExpect(jsonPath("$.error").value("Internal Server Error"))
						.andExpect(jsonPath("$.message").value(SANITIZED_SERVER_ERROR))
						.andExpect(jsonPath("$.path").value("/test/constraint-declaration"));
	}

	@Test
	@DisplayName("ConstraintDefinitionException → 500 with sanitized message")
	void whenConstraintDefinition_ShouldReturn500() throws Exception {
		_mockMvc.perform(get("/test/constraint-definition"))
						.andExpect(status().isInternalServerError())
						.andExpect(jsonPath("$.status").value(500))
						.andExpect(jsonPath("$.error").value("Internal Server Error"))
						.andExpect(jsonPath("$.message").value(SANITIZED_SERVER_ERROR))
						.andExpect(jsonPath("$.path").value("/test/constraint-definition"));
	}

	@Test
	@DisplayName("IOException → 500 with sanitized message")
	void whenIOException_ShouldReturn500() throws Exception {
		_mockMvc.perform(get("/test/io-exception"))
						.andExpect(status().isInternalServerError())
						.andExpect(jsonPath("$.status").value(500))
						.andExpect(jsonPath("$.error").value("Internal Server Error"))
						.andExpect(jsonPath("$.message").value(SANITIZED_SERVER_ERROR))
						.andExpect(jsonPath("$.path").value("/test/io-exception"));
	}

	@Test
	@DisplayName("HttpMessageNotWritableException → 500 with sanitized message")
	void whenMessageNotWritable_ShouldReturn500() throws Exception {
		_mockMvc.perform(get("/test/message-not-writable"))
						.andExpect(status().isInternalServerError())
						.andExpect(jsonPath("$.status").value(500))
						.andExpect(jsonPath("$.error").value("Internal Server Error"))
						.andExpect(jsonPath("$.message").value(SANITIZED_SERVER_ERROR))
						.andExpect(jsonPath("$.path").value("/test/message-not-writable"));
	}

	@Test
	@DisplayName("Unhandled Exception → 500 with sanitized message")
	void whenGenericException_ShouldReturn500() throws Exception {
		_mockMvc.perform(get("/test/generic-exception"))
						.andExpect(status().isInternalServerError())
						.andExpect(jsonPath("$.status").value(500))
						.andExpect(jsonPath("$.error").value("Internal Server Error"))
						.andExpect(jsonPath("$.message").value(SANITIZED_SERVER_ERROR))
						.andExpect(jsonPath("$.path").value("/test/generic-exception"));
	}
}