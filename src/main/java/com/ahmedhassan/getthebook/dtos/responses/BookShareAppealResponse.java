package com.ahmedhassan.getthebook.dtos.responses;

import com.ahmedhassan.getthebook.enums.BookAppealStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
@Schema(name = "BookShareAppealResponse", description = "Response for new book share appeal")
public record BookShareAppealResponse(
				@Schema(description = "Unique identifier of book share appeal", example = "550e8400-e29b-41d4-a716-446655440000")
				UUID appealId,
				@Schema(description = "Unique identifier of user that request for book", example = "550e8400-e29b-41d4-a716-446655440000")
				@NotNull UUID appealBy,
				@Schema(description = "Unique identifier of book that was request user", example = "550e8400-e29b-41d4-a716-446655440000")
				@NotNull UUID bookId,
				@Schema(description = "Enumeration for the current status of appeal", example = "approved")
				BookAppealStatus status,
				@Schema(description = "Timestamp at which appeal was made", example = "2026-03-14T19:40:34.039914217Z")
				Instant bookShareAppealedAt,
				@Schema(description = "Timestamp at which appeal was approved", example = "2026-03-14T19:40:34.039914217Z")
				Instant bookShareAppealApprovedAt

) {
}