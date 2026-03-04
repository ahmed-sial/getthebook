package com.ahmedhassan.getthebook.exceptions;

import jakarta.persistence.EntityNotFoundException;

// CHECK: Should RoleNotFoundException extends EntityNotFoundException or other Exception?
public class RoleNotFoundException extends EntityNotFoundException {
	public RoleNotFoundException() {
	}
	public RoleNotFoundException(String message) {
		super(message);
	}
}