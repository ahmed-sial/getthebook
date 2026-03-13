package com.ahmedhassan.getthebook.specifications;

import com.ahmedhassan.getthebook.entities.Book;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class BookSpecification {
	@Contract(pure = true)
	public static @NonNull Specification<Book> notArchived() {
		return (root, query, cb) ->
						cb.isFalse(root.get("isArchived"));
	}

	@Contract(pure = true)
	public static @NonNull Specification<Book> shareable() {
		return (root, query, cb) ->
						cb.isTrue(root.get("isShareable"));
	}

	@Contract(pure = true)
	public static @NonNull Specification<Book> withoutOwnerId(UUID ownerId) {
		return (root, query, cb) ->
						cb.notEqual(root.get("owner").get("id"), ownerId);
	}

	public static @NonNull Specification<Book> withOwnerId(UUID ownerId) {
		return (root, query, cb) ->
						cb.equal(root.get("owner").get("id"), ownerId);
	}
}