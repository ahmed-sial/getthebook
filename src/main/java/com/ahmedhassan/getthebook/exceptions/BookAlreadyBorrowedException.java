package com.ahmedhassan.getthebook.exceptions;

public class BookAlreadyBorrowedException extends RuntimeException {
	public BookAlreadyBorrowedException() {
	}
	public BookAlreadyBorrowedException(String message) {
		super(message);
	}
}