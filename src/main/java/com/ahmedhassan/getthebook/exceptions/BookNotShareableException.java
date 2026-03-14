package com.ahmedhassan.getthebook.exceptions;

public class BookNotShareableException extends RuntimeException {
	public BookNotShareableException() {
	}
	public BookNotShareableException(String message) {
		super(message);
	}
}