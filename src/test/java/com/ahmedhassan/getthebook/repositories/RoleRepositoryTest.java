package com.ahmedhassan.getthebook.repositories;

import com.ahmedhassan.getthebook.entities.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RoleRepository Tests")
public class RoleRepositoryTest extends BaseRepositoryTest {
	@Autowired private RoleRepository _roleRepository;
	@Autowired private TestEntityManager _entityManager;

	private Role _persistedRole;

	@BeforeEach
	void setUp() {
		_persistedRole = Role
						.builder()
						.name("USER")
						.build();
		_entityManager.persistAndFlush(_persistedRole);
	}

	@Test
	@DisplayName("Should find role by name when role exists")
	void findRoleByName_WhenRoleExists_ShouldReturnRole() {
		var found = _roleRepository.findRoleByName("USER");

		assertThat(found).isPresent();
		assertThat(found.get().getName()).isEqualTo("USER");
	}

	@Test
	@DisplayName("Should not find role by name when role doesn't exist")
	void findRoleByName_WhenRoleDoesNotExist_ShouldReturnNull() {
		var found = _roleRepository.findRoleByName("DOESNOTEXIST");
		assertThat(found).isEmpty();
	}

	@Test
	@DisplayName("Should return true if role exists by name")
	void existsRoleByName_WhenRoleExists_ShouldReturnTrue() {
		var isPresent = _roleRepository.existsRoleByName("USER");
		assertThat(isPresent).isTrue();
	}

	@Test
	@DisplayName("Should return true if role exists by name")
	void existsRoleByName_WhenRoleNotExists_ShouldReturnFalse() {
		var isPresent = _roleRepository.existsRoleByName("DOESNOTEXIST");
		assertThat(isPresent).isFalse();
	}
}