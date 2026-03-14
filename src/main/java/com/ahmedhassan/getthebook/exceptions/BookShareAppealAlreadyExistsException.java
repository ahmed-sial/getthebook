package com.ahmedhassan.getthebook.exceptions;

public class BookShareAppealAlreadyExistsException extends RuntimeException {
	public BookShareAppealAlreadyExistsException() {
	}
	public BookShareAppealAlreadyExistsException(String message) {
		super(message);
	}
}