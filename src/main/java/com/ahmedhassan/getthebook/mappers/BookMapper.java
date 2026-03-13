package com.ahmedhassan.getthebook.mappers;

import com.ahmedhassan.getthebook.dtos.requests.BookRequest;
import com.ahmedhassan.getthebook.dtos.responses.BookResponse;
import com.ahmedhassan.getthebook.dtos.responses.PagedResponse;
import com.ahmedhassan.getthebook.entities.Book;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;

@Slf4j
public class BookMapper {
	public static BookResponse toBookResponse(@NonNull Book book) {
		log.info("Converting Book to BookResponse");
		return BookResponse
						.builder()
						.id(book.getId())
						.title(book.getTitle())
						.genre(book.getGenre())
						.isbn(book.getIsbn())
						.author(book.getAuthor())
						.synopsis(book.getSynopsis())
						.publisher(book.getPublisher())
						.publicationDate(book.getPublicationDate())
						.bookCover(book.getBookCover())
						.ownerId(book.getUser().getId())
						.isShareable(book.getIsShareable())
						.isArchived(book.getIsArchived())
						.build();
	}

	public static PagedResponse<BookResponse> toPagedBookResponse(@NonNull Page<Book> books) {
		log.info("Converting Books to PagedResponse");
		var bookResponse = books
						.stream()
						.map(BookMapper::toBookResponse)
						.toList();
		return new PagedResponse<>(
						bookResponse,
						books.getNumber(),
						books.getSize(),
						books.getTotalElements(),
						books.getTotalPages(),
						books.isFirst(),
						books.isLast()
		);
	}

	public static Book toBook(@NonNull BookRequest request) {
		log.info("Converting BookRequest to Book");
		return Book
						.builder()
						.title(request.title())
						.genre(request.genre())
						.isbn(request.isbn())
						.author(request.author())
						.synopsis(request.synopsis())
						.publisher(request.publisher())
						.publicationDate(request.publicationDate())
						// TODO: .bookCover()
						.isArchived(request.isArchived())
						.isShareable(request.isShareable())
						.build();
	}
}