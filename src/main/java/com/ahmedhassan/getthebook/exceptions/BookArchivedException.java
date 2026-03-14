package com.ahmedhassan.getthebook.exceptions;

public class BookArchivedException extends RuntimeException {
	public BookArchivedException() {
	}
	public BookArchivedException(String message) {
		super(message);
	}
}