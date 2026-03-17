package com.ahmedhassan.getthebook.exceptions;

public class BookShareRecordNotFound extends RuntimeException {
    public BookShareRecordNotFound() {}

    public BookShareRecordNotFound(String message) {
        super(message);
    }
}
