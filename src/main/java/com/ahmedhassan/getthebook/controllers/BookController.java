package com.ahmedhassan.getthebook.controllers;

import com.ahmedhassan.getthebook.annotations.openapi.composed.ApiGetOperation;
import com.ahmedhassan.getthebook.annotations.openapi.composed.ApiSaveOperation;
import com.ahmedhassan.getthebook.annotations.openapi.composed.ApiUpdateOperation;
import com.ahmedhassan.getthebook.dtos.requests.BookRequest;
import com.ahmedhassan.getthebook.dtos.requests.BookUpdateRequest;
import com.ahmedhassan.getthebook.dtos.responses.BookResponse;
import com.ahmedhassan.getthebook.dtos.responses.PagedBookResponse;
import com.ahmedhassan.getthebook.dtos.responses.PagedResponse;
import com.ahmedhassan.getthebook.entities.User;
import com.ahmedhassan.getthebook.services.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.ahmedhassan.getthebook.utils.Utils.maskEmail;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Book", description = "Manage books in the system")
public class BookController {

	private final BookService _bookService;

	@SecurityRequirement(name = "Bearer Authentication")
	@GetMapping
	@Operation(summary = "Fetch paged response of all books", description = "Fetch paged response of all saved books except current logged in user's books")
	@ApiResponse(responseCode = "200", description = "Books fetched successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagedBookResponse.class)))
	@ApiGetOperation
	public ResponseEntity<PagedResponse<BookResponse>> fetchAllBooksExceptCurrentUser(
			@Parameter(description = "Page number (0-based index)", example = "0") @RequestParam(name = "page", defaultValue = "0", required = false) @Min(0) Integer pageNumber,
			@Parameter(description = "Number of records per page", example = "10") @RequestParam(name = "size", defaultValue = "10", required = false) @Max(50) Integer pageSize,
			@Parameter(hidden = true) @AuthenticationPrincipal @NonNull User user) {
		log.info("Fetch all books request received for user email={}", maskEmail(user.getEmail()));
		var response = _bookService.findAllBooksExceptCurrentUser(pageNumber, pageSize, user);
		log.info("Fetch all books request executed successfully");
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(response);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@GetMapping("{book-id}")
	@Operation(summary = "Fetch single book", description = "Fetch single book by it's ID")
	@ApiResponse(responseCode = "200", description = "Book fetched successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponse.class)))
	@ApiGetOperation
	public ResponseEntity<BookResponse> fetchSingleBook(
			@Parameter(description = "Unique identifier of book", example = "550e8400-e29b-41d4-a716-446655440000", required = true, in = ParameterIn.PATH) @PathVariable("book-id") UUID bookId,
			@Parameter(hidden = true) @AuthenticationPrincipal @NonNull User user) {
		log.info("Fetch single book request received for user email={}", maskEmail(user.getEmail()));
		var response = _bookService.findSingleBookById(bookId);
		log.info("Fetch single book request executed successfully");
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(response);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@PostMapping
	@Operation(summary = "Create a new book", description = "Create a new book entry for the current logged in user")
	@ApiResponse(responseCode = "201", description = "New book record saved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponse.class)))
	@ApiSaveOperation
	public ResponseEntity<BookResponse> createNewBook(
			@RequestBody @Valid @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "New book's details", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookRequest.class))) BookRequest bookRequest,
			@Parameter(hidden = true) @AuthenticationPrincipal @NonNull User user) {
		log.info("Create new book request received for user email={}", maskEmail(user.getEmail()));
		var response = _bookService.createNewBook(bookRequest, user);
		log.info("Create new book request executed successfully");
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(response);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "Update a book", description = "Update an existing book by provided ID for current logged in user")
	@PatchMapping("{book-id}")
	@ApiResponse(responseCode = "200", description = "Book updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponse.class)))
	@ApiUpdateOperation
	public ResponseEntity<BookResponse> updateBook(
			@Parameter(description = "Unique identifier of book", example = "550e8400-e29b-41d4-a716-446655440000", required = true, in = ParameterIn.PATH) @PathVariable("book-id") UUID bookId,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Details to update book", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookUpdateRequest.class))) @RequestBody BookUpdateRequest bookRequest,
			@Parameter(hidden = true) @AuthenticationPrincipal @NonNull User user) {
		log.info("Update book request received for user email={}", maskEmail(user.getEmail()));
		var response = _bookService.updateBook(bookId, bookRequest);
		log.info("Update book request executed successfully");
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(response);
	}

	@DeleteMapping("{book-id}")
	public ResponseEntity<UUID> deleteBook(
			@PathVariable("book-id") UUID bookId,
			@AuthenticationPrincipal @NonNull User user) {
		log.info("Delete book request received for user email={}", maskEmail(user.getEmail()));
		var response = _bookService.deleteBook(bookId);
		log.info("Delete book request executed successfully");
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(response);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "Fetch books by owner", description = "Fetch paged response of all the books of current logged in user")
	@GetMapping("/me")
	@ApiResponse(responseCode = "200", description = "Books fetched successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagedBookResponse.class)))
	@ApiGetOperation
	public ResponseEntity<PagedResponse<BookResponse>> fetchBooksByOwner(
			@Parameter(description = "Page number (0-based index)", example = "0") @RequestParam(name = "page", defaultValue = "0", required = false) @Min(0) Integer pageNumber,
			@Parameter(description = "Number of records per page", example = "10") @RequestParam(name = "size", defaultValue = "10", required = false) @Max(50) Integer pageSize,
			@Parameter(hidden = true) @AuthenticationPrincipal @NonNull User user) {
		log.info("Fetch all books for current user request received email={}", maskEmail(user.getEmail()));
		var response = _bookService.findAllBooksByOwner(pageNumber, pageSize, user);
		log.info("Fetch all books for current user request executed successfully");
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(response);
	}

	@PatchMapping("{book-id}/share-toggle")
	public ResponseEntity<BookResponse> toggleBookSharingStatus(@PathVariable("book-id") UUID bookId,
			@AuthenticationPrincipal @NonNull User user) {
		log.info("Toggle book's sharing status request recieved for email={}", maskEmail(user.getEmail()));
		var response = _bookService.toggleBookSharingStatus(bookId);
		log.info("Toggle book's sharing status request executed successfully");
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PatchMapping("{book-id}/archive-toggle")
	public ResponseEntity<BookResponse> toggleBookArchiveStatus(@PathVariable("book-id") UUID bookId,
			@AuthenticationPrincipal @NonNull User user) {
		log.info("Toggle book's archive status request recieved for email={}", maskEmail(user.getEmail()));
		var response = _bookService.toggleBookArchiveStatus(bookId);
		log.info("Toggle book's archive status request executed successfully");
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
// TODO: Implement sorting and filtering