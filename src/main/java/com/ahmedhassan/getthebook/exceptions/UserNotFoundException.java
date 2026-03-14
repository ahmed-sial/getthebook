package com.ahmedhassan.getthebook.exceptions;

public class UserNotFoundException extends RuntimeException {
	public UserNotFoundException() {
	}
	public UserNotFoundException(String message) {
		super(message);
	}
}