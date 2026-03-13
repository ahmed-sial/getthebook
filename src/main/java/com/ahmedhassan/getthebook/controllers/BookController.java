package com.ahmedhassan.getthebook.controllers;

import com.ahmedhassan.getthebook.annotations.openapi.composed.ApiGetOperation;
import com.ahmedhassan.getthebook.dtos.responses.BookResponse;
import com.ahmedhassan.getthebook.dtos.responses.PagedBookResponse;
import com.ahmedhassan.getthebook.dtos.responses.PagedResponse;
import com.ahmedhassan.getthebook.entities.User;
import com.ahmedhassan.getthebook.services.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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

	private final BookService _bookService;

	@SecurityRequirement(name = "Bearer Authentication")
	@GetMapping
	@Operation(
					summary = "Fetch paged response of all books",
					description = "Fetch paged response of all saved books except current logged in user's books"
	)
	@ApiResponse(
					responseCode = "200",
					description = "Books fetched successfully",
					content = @Content(
									mediaType = "application/json",
									schema = @Schema(implementation = PagedBookResponse.class)
					)
	)
	@ApiGetOperation
	public ResponseEntity<PagedResponse<BookResponse>> fetchAllBooksExceptCurrentUser(
					@Parameter(
									description = "Page number (0-based index)",
									example = "0"
					)
					@RequestParam(name = "page", defaultValue = "0", required = false)
					@Min(0) int pageNumber,
					@Parameter(
									description = "Number of records per page",
									example = "10"
					)
					@RequestParam(name = "size", defaultValue = "10", required = false)
					@Max(50) int pageSize,
					@Parameter(hidden = true)
					@AuthenticationPrincipal @NonNull User user
	) {
		log.info("Fetch all books request received for user email={}", maskEmail(user.getEmail()));
		var response = _bookService.findAllBooksExceptCurrentUser(pageNumber, pageSize, user);
		log.info("Fetch all books request executed successfully");
		return ResponseEntity
						.status(HttpStatus.OK)
						.body(response);
	}

	// CONTINUE

}
// TODO: Implement sorting and filtering