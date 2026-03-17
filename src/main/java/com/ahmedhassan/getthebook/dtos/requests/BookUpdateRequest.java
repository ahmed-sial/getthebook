package com.ahmedhassan.getthebook.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "BookUpdateRequest", description = "Request for book update")
public record BookUpdateRequest(
				@Schema(description = "Genre of new book", example = "Romance")
				String genre,

				@Schema(description = "Concise summary of entire book")
				String synopsis,

				// TODO: Book cover

				@Schema(description = "Is new book archived?", example = "true")
				Boolean isArchived,

				@Schema(description = "Is new book allowed to share?", example = "true")
				Boolean isShareable
) {
}