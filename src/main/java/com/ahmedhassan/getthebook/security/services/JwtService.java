package com.ahmedhassan.getthebook.security.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

	@Value("${application.security.jwt.key}")
	private String secretKey;
	@Value("${application.security.jwt.expiration}")
	private Long tokenExpiration;

	private @NonNull SecretKey getSecretKey() {
		final byte[] key = Decoders.BASE64.decode(this.secretKey);
		return Keys.hmacShaKeyFor(key);
	};

	public Claims extractUserClaimsFromJwtToken(String jwtToken) {
		return Jwts
						.parser()
						.verifyWith(getSecretKey())
						.build()
						.parseSignedClaims(jwtToken)
						.getPayload();
	}

	private boolean isJwtTokenExpired(String jwtToken) {
		log.info("Checking if token has expired");
		final var expiryDate = this.extractUserClaimFromJwtToken(jwtToken, Claims::getExpiration);
		var isExpired = expiryDate.before(new Date());
		if (isExpired) {
			log.debug("Token expired");
			return true;
		}
		log.info("Token not expired");
		return false;
	}

	public boolean isJwtTokenValid(String jwtToken, @NonNull UserDetails userDetails) {
		final String username = this.getUsernameFromJwtToken(jwtToken);
		log.info("Checking if token is valid");
		var isValid = userDetails.getUsername().equals(username) && !this.isJwtTokenExpired(jwtToken);
		if (isValid) {
			log.info("Token is valid");
			return true;
		}
		log.info("Token not valid");
		return false;
	}

	public String generateJwtToken(UserDetails userDetails) {
		return generateJwtToken(new HashMap<>(), userDetails);
	}

	public String generateJwtToken(Map<String, Object> claims, @NonNull UserDetails userDetails) {
		log.info("Getting started to generate new JWT token");
		log.info("Compiling user information for JWT token");
		var jwt = Jwts
						.builder()
						.subject(userDetails.getUsername())
						.claims(claims)
						.issuedAt(new Date(System.currentTimeMillis()))
						.expiration(new Date(System.currentTimeMillis() + this.tokenExpiration))
						.signWith(getSecretKey())
						.compact();
		log.info("Generated JWT token successfully");
		return jwt;
	}

	public String getUsernameFromJwtToken(String jwtToken) {
		return this.extractUserClaimFromJwtToken(jwtToken, Claims::getSubject);
	}

	public <T> T extractUserClaimFromJwtToken(String jwtToken, @NonNull Function<Claims, T> claimsResolver) {
		var claims = extractUserClaimsFromJwtToken(jwtToken);
		return claimsResolver.apply(claims);
	}
}