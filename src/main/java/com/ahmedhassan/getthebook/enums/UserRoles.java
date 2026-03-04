package com.ahmedhassan.getthebook.enums;

public enum UserRoles {
	USER("USER"),
	ADMIN("ADMIN")
	;
	private final String role;
	UserRoles(String role) {
		this.role = role;
	}
}