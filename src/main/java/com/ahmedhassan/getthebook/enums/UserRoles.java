package com.ahmedhassan.getthebook.enums;

public enum UserRoles {
	USER("USER"),
	ADMIN("ADMIN"),
	SYSTEM("SYSTEM")
	;
	private final String role;
	UserRoles(String role) {
		this.role = role;
	}
}