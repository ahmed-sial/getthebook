package com.ahmedhassan.getthebook.controllers;

import com.ahmedhassan.getthebook.annotations.openapi.composed.ApiGetOperation;
import com.ahmedhassan.getthebook.annotations.openapi.composed.ApiSaveOperation;
import com.ahmedhassan.getthebook.annotations.openapi.composed.ApiUpdateOperation;
import com.ahmedhassan.getthebook.dtos.requests.BookShareAppealRequest;
import com.ahmedhassan.getthebook.dtos.responses.BookShareAppealResponse;
import com.ahmedhassan.getthebook.dtos.responses.BookShareResponse;
import com.ahmedhassan.getthebook.dtos.responses.PagedBookShareAppealResponse;
import com.ahmedhassan.getthebook.dtos.responses.PagedResponse;
import com.ahmedhassan.getthebook.entities.User;
import com.ahmedhassan.getthebook.services.BookShareAppealService;
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

@Slf4j
@RestController
@RequestMapping("/appeals")
@RequiredArgsConstructor
@Tag(name = "Book Share Appeal", description = "Manage book sharing appeals/requests")
public class BookShareAppealController {
	private final BookShareAppealService _bookShareAppealService;

	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "Create a new book share appeal", description = "Create a new book sharing appeal/request for the current logged in user")
	@PostMapping
	@ApiResponse(responseCode = "201", description = "Book share appeal created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookShareAppealResponse.class)))
	@ApiSaveOperation
	public ResponseEntity<BookShareAppealResponse> createNewBookShareAppeal(
					@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "New book share appeal details", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookShareAppealRequest.class))) @RequestBody @Valid BookShareAppealRequest appealRequest,
					@Parameter(hidden = true) @AuthenticationPrincipal @NonNull User user) {
		log.info("Create new book share appeal request received for email: {}", maskEmail(user.getEmail()));
		var response = _bookShareAppealService.createNewBookShareAppeal(appealRequest, user);
		log.info("Create new book share appeal request executed successfully");
		return ResponseEntity
						.status(HttpStatus.CREATED)
						.body(response);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "Fetch current user's book share appeals", description = "Fetch paged response of all book share appeals of the current logged in user")
	@GetMapping
	@ApiResponse(responseCode = "200", description = "Book share appeals fetched successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagedBookShareAppealResponse.class)))
	@ApiGetOperation
	public ResponseEntity<PagedResponse<BookShareAppealResponse>> fetchCurrentUserBookShareAppeals(
					@Parameter(description = "Page number (0-based index)", example = "0") @RequestParam(name = "page", defaultValue = "0", required = false) @Min(0) Integer pageNumber,
					@Parameter(description = "Number of records per page", example = "10") @RequestParam(name = "size", defaultValue = "10", required = false) @Max(50) Integer pageSize,
					@Parameter(hidden = true) @AuthenticationPrincipal @NonNull User user) {
		log.info("Fetch current user book share appeals request received for email: {}", maskEmail(user.getEmail()));
		var response = _bookShareAppealService.fetchCurrentUserBookShareAppeal(pageNumber, pageSize, user);
		log.info("Fetch current user book share appeals request executed successfully");
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "Fetch a single book share appeal", description = "Fetch a single book share appeal by its ID")
	@GetMapping("{appeal-id}")
	@ApiResponse(responseCode = "200", description = "Book share appeal fetched successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookShareAppealResponse.class)))
	@ApiGetOperation
	public ResponseEntity<BookShareAppealResponse> fetchSingleBookShareAppeal(
					@Parameter(description = "Unique identifier of the appeal", example = "550e8400-e29b-41d4-a716-446655440000", required = true, in = ParameterIn.PATH) @PathVariable("appeal-id") UUID appealId,
					@Parameter(hidden = true) @AuthenticationPrincipal @NonNull User user) {
		log.info("Fetch single book share appeal request received for email: {}", maskEmail(user.getEmail()));
		var response = _bookShareAppealService.fetchSingleBookShareAppeal(appealId);
		log.info("Fetch single book share appeal request executed successfully");
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "Cancel a book share appeal", description = "Cancel an existing book share appeal by its ID for the current logged in user")
	@DeleteMapping("{appeal-id}")
	@ApiResponse(responseCode = "200", description = "Book share appeal cancelled successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UUID.class)))
	public ResponseEntity<UUID> cancelBookShareAppeal(
					@Parameter(description = "Unique identifier of the appeal", example = "550e8400-e29b-41d4-a716-446655440000", required = true, in = ParameterIn.PATH) @PathVariable("appeal-id") UUID appealId,
					@Parameter(hidden = true) @AuthenticationPrincipal @NonNull User user) {
		log.info("Update single book share appeal request received for email: {}", maskEmail(user.getEmail()));
		var response = _bookShareAppealService.deleteBookShareAppeal(appealId);
		log.info("Update single book share appeal request executed successfully");
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "Approve a book share appeal", description = "Approve an existing book share appeal by its ID")
	@PatchMapping("{appeal-id}/approve")
	@ApiResponse(responseCode = "201", description = "Book share appeal approved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookShareResponse.class)))
	@ApiUpdateOperation
	public ResponseEntity<BookShareResponse> approveBookShareAppeal(
					@Parameter(description = "Unique identifier of the appeal", example = "550e8400-e29b-41d4-a716-446655440000", required = true, in = ParameterIn.PATH) @PathVariable("appeal-id") UUID appealId,
					@Parameter(hidden = true) @AuthenticationPrincipal @NonNull User user) {
		log.info("Approve book share appeal request received for email: {}", maskEmail(user.getEmail()));
		var response = _bookShareAppealService.approveBookShareAppeal(appealId);
		log.info("Approve book share appeal request executed successfully");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@Operation(summary = "Reject a book share appeal", description = "Reject an existing book share appeal by its ID")
	@PatchMapping("{appeal-id}/reject")
	@ApiResponse(responseCode = "200", description = "Book share appeal rejected successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookShareAppealResponse.class)))
	@ApiUpdateOperation
	public ResponseEntity<BookShareAppealResponse> rejectBookShareAppeal(
					@Parameter(description = "Unique identifier of the appeal", example = "550e8400-e29b-41d4-a716-446655440000", required = true, in = ParameterIn.PATH) @PathVariable("appeal-id") UUID appealId,
					@Parameter(hidden = true) @AuthenticationPrincipal @NonNull User user) {
		log.info("Reject book share appeal request received for email: {}", maskEmail(user.getEmail()));
		var response = _bookShareAppealService.rejectBookShareAppeal(appealId);
		log.info("Reject book share appeal request executed successfully");
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}