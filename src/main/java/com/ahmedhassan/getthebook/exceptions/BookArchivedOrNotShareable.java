package com.ahmedhassan.getthebook.exceptions;

public class BookArchivedOrNotShareable extends RuntimeException {
 public BookArchivedOrNotShareable() {
	}
	public BookArchivedOrNotShareable(String message) {
		super(message);
	} 
}
