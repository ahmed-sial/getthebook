package com.ahmedhassan.getthebook.dtos.responses;

import java.time.LocalDate;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "BookResponse", description = "Details of books from system")
public record BookResponse(
				@Schema(description = "Book unique ID", example = "550e8400-e29b-41d4-a716-446655440000")
				UUID id,

				@Schema(description = "Book title", example = "The Great Gatsby")
				String title,

				@Schema(description = "Book genre", example = "Fiction")
				String genre,

				@Schema(description = "Book unique ISBN", example = "978-3-16-148410-0")
				String isbn,

				@Schema(description = "Book author name", example = "F. Scott Fitzgerald")
				String author,

				@Schema(description = "Book synopsis", example = "A story of the fabulously wealthy Jay Gatsby and his love for Daisy Buchanan.")
				String synopsis,

				@Schema(description = "Book publisher name", example = "Scribner")
				String publisher,

				@Schema(description = "Book publishing date", example = "1925-04-10")
				LocalDate publicationDate,

				@Schema(description = "Book cover URL", example = "https://cdn.example.com/covers/the-great-gatsby.jpg")
				String bookCover,

				@Schema(description = "Book allowed to share", example = "true")
				Boolean isShareable,

				@Schema(description = "Book archived", example = "false")
				Boolean isArchived,

				@Schema(description = "Book owner ID", example = "660e9511-f30c-52e5-b827-557766551111")
				UUID ownerId
) {
}