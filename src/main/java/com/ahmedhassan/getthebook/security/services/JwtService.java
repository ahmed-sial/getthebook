package com.ahmedhassan.getthebook.security.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

	@Value("${application.security.jwt.key}")
	private String secretKey;
	@Value("${application.security.jwt.expiration}")
	private Integer tokenExpiration;

	private @NonNull SecretKey getSecretKey() {
		final byte[] key = Decoders.BASE64.decode(this.secretKey);
		return Keys.hmacShaKeyFor(key);
	};

	private Claims extractUserClaimsFromJwtToken(String jwtToken) {
		return Jwts
						.parser()
						.verifyWith(getSecretKey())
						.build()
						.parseSignedClaims(jwtToken)
						.getPayload();
	}

	private boolean isJwtTokenExpired(String jwtToken) {
		final var expiryDate = this.extractUserClaimFromJwtToken(jwtToken, Claims::getExpiration);
		return expiryDate.before(new Date());
	}

	public boolean isJwtTokenValid(String jwtToken, @NonNull UserDetails userDetails) {
		final String username = this.getUsernameFromJwtToken(jwtToken);
		return userDetails.getUsername().equals(username) && !this.isJwtTokenExpired(jwtToken);
	}

	public String generateJwtToken(UserDetails userDetails) {
		return generateJwtToken(new HashMap<>(), userDetails);
	}

	public String generateJwtToken(Map<String, Object> claims, @NonNull UserDetails userDetails) {
		var authorities = userDetails
						.getAuthorities()
						.stream()
						.map(GrantedAuthority::getAuthority)
						.toList();

		return Jwts
						.builder()
						.subject(userDetails.getUsername())
						.claims(claims)
						.claim("authorities", authorities)
						.issuedAt(new Date(System.currentTimeMillis()))
						.expiration(new Date(System.currentTimeMillis() + this.tokenExpiration))
						.signWith(getSecretKey())
						.compact();
	}

	public String getUsernameFromJwtToken(String jwtToken) {
		return this.extractUserClaimFromJwtToken(jwtToken, Claims::getSubject);
	}

	public <T> T extractUserClaimFromJwtToken(String jwtToken, @NonNull Function<Claims, T> claimsResolver) {
		var claims = extractUserClaimsFromJwtToken(jwtToken);
		return claimsResolver.apply(claims);
	}
}