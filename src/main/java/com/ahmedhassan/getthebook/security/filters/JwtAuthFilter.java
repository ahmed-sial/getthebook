package com.ahmedhassan.getthebook.security.filters;

import com.ahmedhassan.getthebook.security.services.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.ahmedhassan.getthebook.utils.Utils.maskEmail;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
	private final JwtService _jwtService;
	private final UserDetailsService _userDetailsService;

	@Override
	protected void doFilterInternal(
					@NonNull HttpServletRequest request,
					@NonNull HttpServletResponse response,
					@NonNull FilterChain filterChain
	) throws ServletException, IOException {
		if (request.getServletPath().startsWith("/auth")) {
			filterChain.doFilter(request, response);
			return;
		}
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.debug("Authorization header not found or JWT token missing");
			filterChain.doFilter(request, response);
			return;
		}
		final String jwtToken = authHeader.substring(7);
		final String email = _jwtService.extractUserClaimFromJwtToken(jwtToken, Claims::getSubject);
		log.info("JWT token found for user email: {}", maskEmail(email));
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			log.info("Authentication not found for user email: {}", email);
			var userDetails = _userDetailsService.loadUserByUsername(email);
			if (_jwtService.isJwtTokenValid(jwtToken, userDetails)) {
				UsernamePasswordAuthenticationToken userAuthToken = new
								UsernamePasswordAuthenticationToken(
								userDetails,
								null,
								userDetails.getAuthorities()
				);
				userAuthToken.setDetails(
								new WebAuthenticationDetailsSource().buildDetails(request)
				);
				log.info("Authentication set for user email: {}", maskEmail(email));
				SecurityContextHolder.getContext().setAuthentication(userAuthToken);
			}
		}
		log.info("Authentication completed for user email: {}", maskEmail(email));
		filterChain.doFilter(request, response);
	}
}