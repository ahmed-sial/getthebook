package com.ahmedhassan.getthebook.specifications;

import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.ahmedhassan.getthebook.entities.BookShare;

public class BookShareSpecification {
  public static Specification<BookShare> withBookId(UUID bookId) {
    return (root, query, cb) -> 
      cb.equal(root.get("book").get("id"), bookId);
  }
}
