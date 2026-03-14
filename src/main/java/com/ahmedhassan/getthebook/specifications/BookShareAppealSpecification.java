package com.ahmedhassan.getthebook.specifications;

import com.ahmedhassan.getthebook.entities.BookShareAppeal;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class BookShareAppealSpecification {
	public static Specification<BookShareAppeal> withUserId(UUID userId) {
		return (root, query, cb) ->
						cb.equal(root.get("user").get("id"), userId);
	}
}