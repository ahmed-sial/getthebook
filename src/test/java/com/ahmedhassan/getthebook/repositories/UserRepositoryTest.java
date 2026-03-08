package com.ahmedhassan.getthebook.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static com.ahmedhassan.getthebook.common.TestDataBuilder.buildRole;
import static com.ahmedhassan.getthebook.common.TestDataBuilder.buildUser;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserRepository Tests")
public class UserRepositoryTest extends BaseRepositoryTest {
	@Autowired private UserRepository _userRepository;
	@Autowired private TestEntityManager _entityManager;

	@BeforeEach
	void setUp() {
		var role = _entityManager.persist(buildRole("USER"));
		_entityManager.persistAndFlush(buildUser(role));
	}

	@Test
	@DisplayName("Should return user for existing email")
	void findUserByEmail_WithExistingEmail_ShouldReturnUser() {
		var user = _userRepository.findUserByEmail("johndoe@example.com");
		assertThat(user).isPresent();
		assertThat(user.get().getFirstName()).isEqualTo("John");
	}

	@Test
	@DisplayName("Should not return user for non existing email")
	void findUserByEmail_WithNonExistingEmail_ShouldReturnUser() {
		var user = _userRepository.findUserByEmail("doesnotexist");
		assertThat(user).isNotPresent();
	}
}