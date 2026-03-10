package com.ahmedhassan.getthebook.security.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Date;
import java.util.Map;

import static com.ahmedhassan.getthebook.common.TestDataBuilder.buildRole;
import static com.ahmedhassan.getthebook.common.TestDataBuilder.buildUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Tests")
public class JwtServiceTest {

	@InjectMocks
	private JwtService jwtService;

	// Must match exactly what's injected via ReflectionTestUtils
	private static final String SECRET_KEY = "dGVzdC1zZWNyZXQta2V5LWZvci11bml0LXRlc3Rpbmctb25seQ==";
	private static final long TOKEN_EXPIRATION = 86400000L; // 24 hours

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);
		ReflectionTestUtils.setField(jwtService, "tokenExpiration", TOKEN_EXPIRATION);
	}


	@Test
	@DisplayName("Should return a non-blank token")
	void generateJwtToken_ShouldReturnNonBlankToken() {
		var user = buildUser(buildRole("USER"));
		var token = jwtService.generateJwtToken(user);
		assertThat(token).isNotBlank();
	}

	@Test
	@DisplayName("Should contain correct subject (username) in token")
	void generateJwtToken_ShouldContainCorrectSubject() {
		var user = buildUser(buildRole("USER"));
		var token = jwtService.generateJwtToken(user);
		assertThat(jwtService.getUsernameFromJwtToken(token)).isEqualTo(user.getUsername());
	}

	@Test
	@DisplayName("Should contain a non-null issuedAt date in token")
	void generateJwtToken_ShouldContainIssuedAtDate() {
		var user = buildUser(buildRole("USER"));
		var token = jwtService.generateJwtToken(user);
		var issuedAt = jwtService.extractUserClaimFromJwtToken(token, Claims::getIssuedAt);
		assertThat(issuedAt).isNotNull();
	}

	@Test
	@DisplayName("Should contain a non-null expiration date in token")
	void generateJwtToken_ShouldContainExpirationDate() {
		var user = buildUser(buildRole("USER"));
		var token = jwtService.generateJwtToken(user);
		var expiration = jwtService.extractUserClaimFromJwtToken(token, Claims::getExpiration);
		assertThat(expiration).isNotNull();
	}

	@Test
	@DisplayName("Should expire after configured time (within 1 second tolerance)")
	void generateJwtToken_ShouldExpireAfterConfiguredTime() {
		var user = buildUser(buildRole("USER"));
		var token = jwtService.generateJwtToken(user);
		var expiry = jwtService.extractUserClaimFromJwtToken(token, Claims::getExpiration);

		long delta = expiry.getTime() - System.currentTimeMillis();

		// delta should be within (TOKEN_EXPIRATION - 1000ms) and TOKEN_EXPIRATION
		assertThat(delta)
						.isLessThanOrEqualTo(TOKEN_EXPIRATION)
						.isGreaterThan(TOKEN_EXPIRATION - 1000);
	}

	@Test
	@DisplayName("Two tokens for same user should not be identical (different issuedAt timestamps)")
	void generateJwtToken_TwoCallsForSameUser_ShouldNotBeIdentical() throws InterruptedException {
		var user = buildUser(buildRole("USER"));
		var token1 = jwtService.generateJwtToken(user);
		Thread.sleep(1000); // ensure different issuedAt
		var token2 = jwtService.generateJwtToken(user);
		assertThat(token1).isNotEqualTo(token2);
	}


	@Test
	@DisplayName("Should include extra claims in token when provided")
	void generateJwtToken_WithExtraClaims_ShouldIncludeThemInToken() {
		var user = buildUser(buildRole("ADMIN"));
		Map<String, Object> extraClaims = Map.of("role", "ADMIN", "customField", "value123");

		var token = jwtService.generateJwtToken(extraClaims, user);

		var role = jwtService.extractUserClaimFromJwtToken(token, c -> c.get("role", String.class));
		var customField = jwtService.extractUserClaimFromJwtToken(token, c -> c.get("customField", String.class));

		assertThat(role).isEqualTo("ADMIN");
		assertThat(customField).isEqualTo("value123");
	}

	@Test
	@DisplayName("Should still contain correct subject when extra claims are provided")
	void generateJwtToken_WithExtraClaims_ShouldStillHaveCorrectSubject() {
		var user = buildUser(buildRole("USER"));
		var token = jwtService.generateJwtToken(Map.of("key", "val"), user);
		assertThat(jwtService.getUsernameFromJwtToken(token)).isEqualTo(user.getUsername());
	}


	@Test
	@DisplayName("Should return true for a valid token with matching user")
	void isJwtTokenValid_WithMatchingUserAndValidToken_ShouldReturnTrue() {
		var user = buildUser(buildRole("USER"));
		var token = jwtService.generateJwtToken(user);
		assertThat(jwtService.isJwtTokenValid(token, user)).isTrue();
	}

	@Test
	@DisplayName("Should return false when username in token does not match userDetails")
	void isJwtTokenValid_WhenUsernameMismatch_ShouldReturnFalse() {
		var alice = buildUser(buildRole("USER"), "alice@example.com");
		var bob = buildUser(buildRole("USER"), "bob@example.com");

		var tokenForAlice = jwtService.generateJwtToken(alice);

		// Bob tries to use Alice's token
		assertThat(jwtService.isJwtTokenValid(tokenForAlice, bob)).isFalse();
	}

	@Test
	@DisplayName("Should return false for an expired token")
	void isJwtTokenValid_WithExpiredToken_ShouldReturnFalse() {
		// Set a negative expiration to immediately expire the token
		ReflectionTestUtils.setField(jwtService, "tokenExpiration", -1000L);
		var user = buildUser(buildRole("USER"));
		var expiredToken = jwtService.generateJwtToken(user);

		// Restore expiration so validation can run without re-throwing on generation
		ReflectionTestUtils.setField(jwtService, "tokenExpiration", TOKEN_EXPIRATION);

		assertThatThrownBy(() -> jwtService.isJwtTokenValid(expiredToken, user))
						.isInstanceOf(ExpiredJwtException.class);
	}


	@Test
	@DisplayName("Should throw JwtException when token signature is tampered")
	void isJwtTokenValid_WhenSignatureTampered_ShouldThrowJwtException() {
		var user = buildUser(buildRole("USER"));
		var token = jwtService.generateJwtToken(user);

		// Replace the last part (signature) with garbage
		var tamperedToken = token.substring(0, token.lastIndexOf('.') + 1) + "invalidsignatureXYZ";

		assertThatThrownBy(() -> jwtService.isJwtTokenValid(tamperedToken, user))
						.isInstanceOf(JwtException.class);
	}

	@Test
	@DisplayName("Should throw JwtException for alg:none (unsigned) token")
	void isJwtTokenValid_WithAlgNoneToken_ShouldThrowJwtException() {
		var user = buildUser(buildRole("USER"));

		// Manually craft an unsigned JWT (alg:none attack)
		String header = Base64.getUrlEncoder().withoutPadding()
						.encodeToString("{\"alg\":\"none\",\"typ\":\"JWT\"}".getBytes());
		String payload = Base64.getUrlEncoder().withoutPadding()
						.encodeToString(("{\"sub\":\"" + user.getUsername() + "\"}").getBytes());
		String unsignedToken = header + "." + payload + ".";

		assertThatThrownBy(() -> jwtService.isJwtTokenValid(unsignedToken, user))
						.isInstanceOf(JwtException.class);
	}

	@Test
	@DisplayName("Should throw JwtException for a completely malformed token")
	void getUsernameFromJwtToken_WithMalformedToken_ShouldThrowJwtException() {
		assertThatThrownBy(() -> jwtService.getUsernameFromJwtToken("this.is.notavalidtoken"))
						.isInstanceOf(JwtException.class);
	}

	@Test
	@DisplayName("Should throw exception for a blank token string")
	void getUsernameFromJwtToken_WithBlankToken_ShouldThrowException() {
		assertThatThrownBy(() -> jwtService.getUsernameFromJwtToken(""))
						.isInstanceOf(Exception.class);
	}

	@Test
	@DisplayName("Should throw exception for a null token")
	void getUsernameFromJwtToken_WithNullToken_ShouldThrowException() {
		assertThatThrownBy(() -> jwtService.getUsernameFromJwtToken(null))
						.isInstanceOf(Exception.class);
	}

	@Test
	@DisplayName("Should reject a token signed with a different secret key")
	void isJwtTokenValid_WhenSignedWithDifferentKey_ShouldThrowJwtException() {
		// Generate token with current key, then switch to a different key for validation
		var user = buildUser(buildRole("USER"));
		var token = jwtService.generateJwtToken(user);

		// Swap in a completely different secret key
		ReflectionTestUtils.setField(jwtService, "secretKey",
						"ZGlmZmVyZW50LXNlY3JldC1rZXktZm9yLXRlc3Rpbmctb25seQ==");

		assertThatThrownBy(() -> jwtService.isJwtTokenValid(token, user))
						.isInstanceOf(JwtException.class);
	}


	@Test
	@DisplayName("Should correctly extract a custom claim by key")
	void extractUserClaimFromJwtToken_ShouldExtractCustomClaim() {
		var user = buildUser(buildRole("USER"));
		var token = jwtService.generateJwtToken(Map.of("department", "engineering"), user);
		var dept = jwtService.extractUserClaimFromJwtToken(token, c -> c.get("department", String.class));
		assertThat(dept).isEqualTo("engineering");
	}

	@Test
	@DisplayName("Should return null for a claim key that does not exist in token")
	void extractUserClaimFromJwtToken_WhenClaimMissing_ShouldReturnNull() {
		var user = buildUser(buildRole("USER"));
		var token = jwtService.generateJwtToken(user);
		var missing = jwtService.extractUserClaimFromJwtToken(token, c -> c.get("nonexistent", String.class));
		assertThat(missing).isNull();
	}
}