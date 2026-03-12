package com.ahmedhassan.getthebook.security.filters;

import com.ahmedhassan.getthebook.entities.User;
import com.ahmedhassan.getthebook.repositories.RoleRepository;
import com.ahmedhassan.getthebook.repositories.UserRepository;
import com.ahmedhassan.getthebook.security.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthFilterTest {
	@Mock private JwtService _jwtService;
	@Mock private UserDetailsService _userDetailsService;
	@Mock private HttpServletRequest _request;
	@Mock private HttpServletResponse _response;
	@Mock private FilterChain _chain;

	@InjectMocks private JwtAuthFilter _jwtAuthFilter;

	private UserDetails _userDetails;
	private static final String _TOKEN = "valid.jwt.token";
	private static final String _EMAIL = "testuser@example.com";

	@BeforeEach
	void setUp() {
		SecurityContextHolder.clearContext();
		_userDetails = User
						.builder()
						.email(_EMAIL)
						.password("password")
						.build();
	}

	@Test
	void shouldBypassFilter_WhenPathStartsWithAuthLogin() throws Exception {
		when(_request.getServletPath()).thenReturn("/auth/login");
		_jwtAuthFilter.doFilterInternal(_request, _response, _chain);

		verify(_chain).doFilter(_request, _response);
		verifyNoInteractions(_jwtService, _userDetailsService);
	}

	@Test
	void shouldBypassFilter_WhenPathStartsWithAuthRegister() throws Exception {
		when(_request.getServletPath()).thenReturn("/auth/register");

		_jwtAuthFilter.doFilterInternal(_request, _response, _chain);

		verify(_chain).doFilter(_request, _response);
		verifyNoInteractions(_jwtService, _userDetailsService);
	}

	@Test
	void shouldContinueFilterChain_WhenAuthorizationHeaderIsNull() throws Exception {
		when(_request.getServletPath()).thenReturn("/api/books");
		when(_request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

		_jwtAuthFilter.doFilterInternal(_request, _response, _chain);

		verify(_chain).doFilter(_request, _response);
		verifyNoInteractions(_jwtService, _userDetailsService);
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
	}

	@Test
	void shouldContinueFilterChain_WhenAuthorizationHeaderIsEmpty() throws Exception {
		when(_request.getServletPath()).thenReturn("/api/books");
		when(_request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("");
		_jwtAuthFilter.doFilterInternal(_request, _response, _chain);
		verify(_chain).doFilter(_request, _response);
		verifyNoInteractions(_jwtService, _userDetailsService);
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
	}

	@Test
	void shouldContinueFilterChain_WhenAuthHeaderNotStartsWithBearer() throws ServletException, IOException {
		when(_request.getServletPath()).thenReturn("/api/books");
		when(_request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("not-bearer");
		_jwtAuthFilter.doFilterInternal(_request, _response, _chain);
		verify(_chain).doFilter(_request, _response);
		verifyNoInteractions(_jwtService, _userDetailsService);
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
	}

	// TODO: Review following test cases
	@Test
	void shouldSetAuthentication_whenJwtTokenIsValid() throws Exception {
		when(_request.getServletPath()).thenReturn("/api/books");
		when(_request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + _TOKEN);
		when(_jwtService.extractUserClaimFromJwtToken(eq(_TOKEN), any(Function.class)))
						.thenReturn(_EMAIL);
		when(_userDetailsService.loadUserByUsername(_EMAIL)).thenReturn(_userDetails);
		when(_jwtService.isJwtTokenValid(_TOKEN, _userDetails)).thenReturn(true);

		_jwtAuthFilter.doFilterInternal(_request, _response, _chain);

		// Authentication must be set in the security context
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
		assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
						.isEqualTo(_EMAIL);

		verify(_chain).doFilter(_request, _response);
	}

	@Test
	void shouldNotSetAuthentication_whenJwtTokenIsInvalid() throws Exception {
		when(_request.getServletPath()).thenReturn("/api/books");
		when(_request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + _TOKEN);
		when(_jwtService.extractUserClaimFromJwtToken(eq(_TOKEN), any(Function.class)))
						.thenReturn(_EMAIL);
		when(_userDetailsService.loadUserByUsername(_EMAIL)).thenReturn(_userDetails);
		when(_jwtService.isJwtTokenValid(_TOKEN, _userDetails)).thenReturn(false);

		_jwtAuthFilter.doFilterInternal(_request, _response, _chain);

		// Authentication must remain null for invalid tokens
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
		verify(_chain).doFilter(_request, _response);
	}

	// ─── Already-authenticated user tests ────────────────────────────────────

	@Test
	void shouldSkipUserDetailsLoad_whenAuthenticationAlreadyExists() throws Exception {
		// Pre-populate the security context (e.g. earlier filter already authenticated)
		var existingAuth = new UsernamePasswordAuthenticationToken(_userDetails, null, Collections.emptyList());
		SecurityContextHolder.getContext().setAuthentication(existingAuth);

		when(_request.getServletPath()).thenReturn("/api/books");
		when(_request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + _TOKEN);
		when(_jwtService.extractUserClaimFromJwtToken(eq(_TOKEN), any(Function.class)))
						.thenReturn(_EMAIL);

		_jwtAuthFilter.doFilterInternal(_request, _response, _chain);

		// UserDetailsService and token validation should be skipped entirely
		verifyNoInteractions(_userDetailsService);
		verify(_jwtService, never()).isJwtTokenValid(any(), any());
		verify(_chain).doFilter(_request, _response);
	}


}