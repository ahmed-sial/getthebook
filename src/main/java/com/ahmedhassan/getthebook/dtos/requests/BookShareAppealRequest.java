package com.ahmedhassan.getthebook.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(name = "BookShareAppealRequest", description = "Request for new book share appeal")
public record BookShareAppealRequest(
				@Schema(description = "Unique identifier of user that request for book", example = "550e8400-e29b-41d4-a716-446655440000")
				@NotNull UUID appealBy,
				@Schema(description = "Unique identifier of book that was request user", example = "550e8400-e29b-41d4-a716-446655440000")
				@NotNull UUID bookId
) {
}