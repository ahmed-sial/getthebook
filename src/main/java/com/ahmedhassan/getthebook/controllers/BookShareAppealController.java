package com.ahmedhassan.getthebook.controllers;

import com.ahmedhassan.getthebook.dtos.requests.BookShareAppealRequest;
import com.ahmedhassan.getthebook.entities.BookShareAppeal;
import com.ahmedhassan.getthebook.entities.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ahmedhassan.getthebook.utils.Utils.maskEmail;

@Slf4j
@RestController
@RequestMapping("/borrows")
@RequiredArgsConstructor
@Tag(name = "Book Share Appeal", description = "Manage book sharing appeals/requests")
public class BookShareAppealController {

	@PostMapping
	public ResponseEntity<?> createNewBookShareAppeal(
					@RequestBody @Valid BookShareAppealRequest appealRequest,
					@AuthenticationPrincipal @NonNull User user
	) {
		log.info("Create new book share appeal request received for email: {}", maskEmail(user.getEmail()));
	}

}