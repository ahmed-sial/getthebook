package com.ahmedhassan.getthebook.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Builder
@Schema(name = "BookResponse", description = "Details of books from system")
public record BookResponse(
				@Schema(description = "Book unique ID")
				UUID id,
				@Schema(description = "Book title")
				String title,
				@Schema(description = "Book genre")
				String genre,
				@Schema(description = "Book unique ISBN")
				String isbn,
				@Schema(description = "Book author name")
				String author,
				@Schema(description = "Book synopsis")
				String synopsis,
				@Schema(description = "Book publisher name")
				String publisher,
				@Schema(description = "Book publishing date")
				LocalDate publicationDate,
				@Schema(description = "Book cover URL")
				String bookCover,
				@Schema(description = "Book allowed to share")
				Boolean isShareable,
				@Schema(description = "Book archived")
				Boolean isArchived,
				@Schema(description = "Book owner ID")
				UUID ownerId
) {
}