package com.ahmedhassan.getthebook.dtos.responses;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "BookShareResponse", description = "Details of a book share record")
public record BookShareResponse(
				@Schema(description = "Book share record unique ID", example = "550e8400-e29b-41d4-a716-446655440000")
				UUID id,

				@Schema(description = "ID of the user the book was shared to", example = "660e9511-f30c-52e5-b827-557766551111")
				UUID sharedTo,

				@Schema(description = "ID of the shared book", example = "770f0622-g41d-63f6-c938-668877662222")
				UUID bookId,

				@Schema(description = "Timestamp when the book was shared", example = "2024-01-15T10:30:00Z")
				Instant sharedAt,

				@Schema(description = "Timestamp when the book share expires", example = "2024-07-15T10:30:00Z")
				Instant expiresAt
) {}