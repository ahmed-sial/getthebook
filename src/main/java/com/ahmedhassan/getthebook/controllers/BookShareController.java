package com.ahmedhassan.getthebook.controllers;

import java.util.UUID;

import com.ahmedhassan.getthebook.annotations.openapi.composed.ApiGetOperation;
import com.ahmedhassan.getthebook.entities.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ahmedhassan.getthebook.dtos.responses.BookShareResponse;
import com.ahmedhassan.getthebook.services.BookShareService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/shares")
@RequiredArgsConstructor
@Tag(name = "Book Share", description = "Manage book share records")
public class BookShareController {

  private final BookShareService _bookShareService;

  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(summary = "Fetch a single book share record", description = "Fetch a single book share record by its ID")
  @GetMapping("{share-id}")
  @ApiResponse(responseCode = "200", description = "Book share record fetched successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookShareResponse.class)))
  @ApiGetOperation
  public ResponseEntity<BookShareResponse> fetchSingleBookShareRecord(
          @Parameter(description = "Unique identifier of the book share record", example = "550e8400-e29b-41d4-a716-446655440000", required = true, in = ParameterIn.PATH) @PathVariable("share-id") UUID bookShareId,
          @Parameter(hidden = true) @NonNull @AuthenticationPrincipal User user
  ) {
    log.info("Fetch single book share record request received for share id={}", bookShareId);
    var response = _bookShareService.getSingleBookShareRecord(bookShareId);
    log.info("Fetch single book share record request executed successfully");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}