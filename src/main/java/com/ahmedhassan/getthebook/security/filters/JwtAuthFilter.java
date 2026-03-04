package com.ahmedhassan.getthebook.security.filters;

import com.ahmedhassan.getthebook.security.services.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(
					@NonNull HttpServletRequest request,
					@NonNull HttpServletResponse response,
					@NonNull FilterChain filterChain
	) throws ServletException, IOException {
		if (request.getServletPath().contains("/api/v1/auth")) {
			filterChain.doFilter(request, response);
			return;
		}
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		final String jwtToken = authHeader.substring(7);
		final String email = jwtService.extractUserClaimFromJwtToken(jwtToken, Claims::getSubject);
		if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			var userDetails = userDetailsService.loadUserByUsername(email);
			if (jwtService.isJwtTokenValid(jwtToken, userDetails)) {
				UsernamePasswordAuthenticationToken userAuthToken = new
								UsernamePasswordAuthenticationToken(
								userDetails,
								null,
								userDetails.getAuthorities()
				);
				userAuthToken.setDetails(
								new WebAuthenticationDetailsSource().buildDetails(request)
				);
				SecurityContextHolder.getContext().setAuthentication(userAuthToken);
			}
		}
		filterChain.doFilter(request, response);
	}
}