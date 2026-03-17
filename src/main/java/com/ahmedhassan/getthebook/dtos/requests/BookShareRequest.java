package com.ahmedhassan.getthebook.dtos.requests;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(name = "BookShareRequest", description = "Request for new book share record")
public record BookShareRequest(
				@Schema(description = "Unique identifier of book that was request user", example = "550e8400-e29b-41d4-a716-446655440000")
				@NotNull(message = "Kindly enter valid book ID")
				UUID bookId
) {}