package com.ahmedhassan.getthebook.dtos.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Date;

@Builder
@Schema(name = "BookRequest", description = "Request for new book registration")
public record BookRequest(
				@NotBlank(message = "Title must not be blank")
				@Size(min = 2, max = 100, message = "Title must be between 2 to 100 characters long")
				@Schema(description = "Title of new book", example = "Pride and Prejudice")
				String title,
				@NotBlank(message = "Genre must not be blank")
				@Size(min = 2, max = 20, message = "Genre must be between 2 to 20 characters long")
				@Schema(description = "Genre of new book", example = "Romance")
				String genre,
				@NotBlank(message = "ISBN must not be blank")
				@Size(min = 13, max = 13, message = "ISBN must be 13 characters long")
				@Schema(description = "ISBN of new book", example = "9783161484100")
				String isbn,
				@NotBlank(message = "Author name must not be blank")
				@Size(min = 2, max = 100, message = "Author name must be between 2 to 100 characters long")
				@Schema(description = "Author's name of new book", example = "Jane Austen")
				String author,
				@NotBlank(message = "Synopsis must not be blank")
				@Size(min = 20, max = 255, message = "Synopsis must be between 20 to 100 characters long")
				@Schema(description = "Concise summary of entire book")
				String synopsis,
				@NotBlank(message = "Publisher name must not be blank")
				@Size(min = 2, max = 100, message = "Publisher name must be between 2 to 100 characters long")
				@Schema(description = "Publisher's name of new book", example = "Penguin Classics")
				String publisher,
				@JsonFormat(pattern = "yyyy-MM-dd")
				@Schema(description = "Publication date of new book", example = "2000-01-01")
				LocalDate publicationDate,
				// TODO: Book cover
				@Schema(description = "Is new book archived?", example = "true")
				Boolean isArchived,
				@Schema(description = "Is new book allowed to share?", example = "true")
				Boolean isShareable
) {
}