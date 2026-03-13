package com.ahmedhassan.getthebook.exceptions;

public class BookNotFoundException extends RuntimeException {
	public BookNotFoundException() {
	}
	public BookNotFoundException(String message) {
		super(message);
	}
}