package com.ahmedhassan.getthebook.controllers;

import com.ahmedhassan.getthebook.dtos.requests.BookShareAppealRequest;
import com.ahmedhassan.getthebook.dtos.responses.BookShareAppealResponse;
import com.ahmedhassan.getthebook.dtos.responses.PagedResponse;
import com.ahmedhassan.getthebook.entities.User;
import com.ahmedhassan.getthebook.services.BookShareAppealService;
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

	@PostMapping
	public ResponseEntity<BookShareAppealResponse> createNewBookShareAppeal(
					@RequestBody @Valid BookShareAppealRequest appealRequest,
					@AuthenticationPrincipal @NonNull User user
	) {
		log.info("Create new book share appeal request received for email: {}", maskEmail(user.getEmail()));
		var response = _bookShareAppealService.createNewBookShareAppeal(appealRequest, user);
		log.info("Create new book share appeal request executed successfully");
		return ResponseEntity
						.status(HttpStatus.CREATED)
						.body(response);
	}

	@GetMapping
	public ResponseEntity<PagedResponse<BookShareAppealResponse>> fetchCurrentUserBookShareAppeals(
					@RequestParam(name = "page", defaultValue = "0", required = false)
					@Min(0) Integer pageNumber,
					@RequestParam(name = "size", defaultValue = "10", required = false)
					@Max(50) Integer pageSize,
					@AuthenticationPrincipal @NonNull User user
	) {
		log.info("Fetch current user book share appeals request received for email: {}", maskEmail(user.getEmail()));
		var response = _bookShareAppealService.fetchCurrentUserBookShareAppeal(pageNumber, pageSize, user);
		log.info("Fetch current user book share appeals request executed successfully");
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("{appeal-id}")
	public ResponseEntity<BookShareAppealResponse> fetchSingleBookShareAppeal(
					@PathVariable("appeal-id") UUID appealId,
					@AuthenticationPrincipal @NonNull User user
	) {
		log.info("Fetch single book share appeal request received for email: {}", maskEmail(user.getEmail()));
		var response = _bookShareAppealService.fetchSingleBookShareAppeal(appealId, user);
		log.info("Fetch single book share appeal request executed successfully");
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	// TODO: List all the pending appeals
	// TODO: List all the approved appeals
	// TODO: Approve a pending request
	// TODO: Reject a pending request

}