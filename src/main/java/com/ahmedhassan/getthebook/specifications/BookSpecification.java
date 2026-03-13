package com.ahmedhassan.getthebook.specifications;

import com.ahmedhassan.getthebook.entities.Book;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class BookSpecification {
	public static Specification<Book> notArchived() {
		return (root, query, cb) ->
						cb.isFalse(root.get("isArchived"));
	}

	public static Specification<Book> shareable() {
		return (root, query, cb) ->
						cb.isTrue(root.get("isShareable"));
	}

	public static Specification<Book> withoutOwnerId(UUID ownerId) {
		return (root, query, cb) ->
						cb.notEqual(root.get("owner").get("id"), ownerId);
	}
}