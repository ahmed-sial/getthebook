package com.ahmedhassan.getthebook.common;

import com.ahmedhassan.getthebook.dtos.requests.RegisterRequest;
import com.ahmedhassan.getthebook.entities.Role;
import com.ahmedhassan.getthebook.entities.User;

public class TestDataBuilder {

	// Role
	public static Role buildRole(String name) {
		return Role
						.builder()
						.name(name)
						.build();
	}

	public static User buildUser(Role role) {
		return User
						.builder()
						.firstName("John")
						.lastName("Doe")
						.email("johndoe@example.com")
						.password("password")
						.isAccountLocked(false)
						.isAccountEnabled(true)
						.role(role)
						// Add new required relationship here
						.build();
	}
	public static RegisterRequest buildRegisterRequest() {
		return RegisterRequest
						.builder()
						.firstName("John")
						.lastName("Doe")
						.email("johndoe@@example.com")
						.password("password")
						.build();
	}
}