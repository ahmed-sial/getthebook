package com.ahmedhassan.getthebook.controllers;

import com.ahmedhassan.getthebook.dtos.responses.BookResponse;
import com.ahmedhassan.getthebook.dtos.responses.PagedResponse;
import com.ahmedhassan.getthebook.entities.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ahmedhassan.getthebook.utils.Utils.maskEmail;

@Slf4j
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Book", description = "Manage books in the system")
public class BookController {

	public ResponseEntity<PagedResponse<BookResponse>> fetchAllBooks(
					@RequestParam(name = "page", defaultValue = "0", required = false) int pageNumber,
					@RequestParam(name = "size", defaultValue = "10", required = false) int pageSize,
					@AuthenticationPrincipal @NonNull User user
	) {
		log.info("Fetch all books request received for user email={}", maskEmail(user.getEmail()));
		return null;
	}

}