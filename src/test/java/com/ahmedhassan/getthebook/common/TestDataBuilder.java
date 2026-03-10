package com.ahmedhassan.getthebook.common;

import com.ahmedhassan.getthebook.dtos.requests.LoginRequest;
import com.ahmedhassan.getthebook.dtos.requests.RegisterRequest;
import com.ahmedhassan.getthebook.dtos.responses.LoginResponse;
import com.ahmedhassan.getthebook.dtos.responses.RegisterResponse;
import com.ahmedhassan.getthebook.entities.Role;
import com.ahmedhassan.getthebook.entities.User;

import java.util.UUID;

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
						.password("password123")
						.isAccountLocked(false)
						.isAccountEnabled(true)
						.role(role)
						// Add new required relationship here
						.build();
	}

	public static User buildUser(Role role, String email) {
		return User
						.builder()
						.firstName("John")
						.lastName("Doe")
						.email(email)
						.password("password123")
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
						.email("johndoe@example.com")
						.password("password123")
						.build();
	}

	public static RegisterRequest buildInvalidRegisterRequest() {
		return RegisterRequest
						.builder()
						.firstName("John")
						.lastName("Doe")
						.email("not-an-email")
						.password("password123")
						.build();
	}

	public static RegisterResponse buildRegisterResponse() {
		return RegisterResponse
						.builder()
						.id(UUID.randomUUID())
						.firstName("John")
						.lastName("Doe")
						.email("johndoe@example.com")
						.build();
	}

	public static LoginRequest buildLoginRequest() {
		return LoginRequest
						.builder()
						.email("johndoe@example.com")
						.password("password123")
						.build();
	}

	public static LoginRequest buildInvalidLoginRequest() {
		return LoginRequest
						.builder()
						.email("not-an-email")
						.password("password123")
						.build();
	}

	public static LoginResponse buildLoginResponse() {
		return LoginResponse
						.builder()
						.id(UUID.randomUUID())
						.firstName("John")
						.lastName("Doe")
						.email("johndoe@example.com")
						.token("json-token-xyz")
						.build();
	}
}