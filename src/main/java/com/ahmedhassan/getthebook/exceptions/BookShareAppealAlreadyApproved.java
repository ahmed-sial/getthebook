package com.ahmedhassan.getthebook.exceptions;

public class BookShareAppealAlreadyApproved extends RuntimeException {
  public BookShareAppealAlreadyApproved() {}

  public BookShareAppealAlreadyApproved(String message) {
    super(message);
  }
}
