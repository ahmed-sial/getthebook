package com.ahmedhassan.getthebook.services;

import com.ahmedhassan.getthebook.dtos.requests.BookRequest;
import com.ahmedhassan.getthebook.dtos.requests.BookUpdateRequest;
import com.ahmedhassan.getthebook.dtos.responses.BookResponse;
import com.ahmedhassan.getthebook.dtos.responses.PagedResponse;
import com.ahmedhassan.getthebook.entities.User;
import com.ahmedhassan.getthebook.exceptions.BookNotFoundException;
import com.ahmedhassan.getthebook.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.ahmedhassan.getthebook.mappers.BookMapper.*;
import static com.ahmedhassan.getthebook.specifications.BookSpecification.*;
import static com.ahmedhassan.getthebook.utils.Utils.isValidLength;
import static com.ahmedhassan.getthebook.utils.Utils.validateAccessToResource;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

	private final BookRepository _bookRepository;

	public PagedResponse<BookResponse> findAllBooksExceptCurrentUser(
					Integer pageNumber,
					Integer pageSize,
					@NonNull User user
	) {
		log.info("Compiling paged request...");
		var pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt")
						.descending());
		var spec = Specification.allOf(
						shareable(),
						notArchived(),
						withoutUserId(user.getId())
		);
		log.info("Fetching all the books for paged response, pageNumber={}, pageSize={}", pageNumber, pageSize);
		var books = _bookRepository.findAll(spec, pageable);
		return toPagedBookResponse(books);
	}
	// TODO: Should only be accessed by owner if is archived and not shareable else can be accessed by anyone.
	public BookResponse findSingleBookById(UUID bookId, User user) {
		log.info("Fetching book response for book id = {}", bookId);
		var book = _bookRepository.findById(bookId)
						.orElseThrow(() -> {
							log.debug("Book not found for id={}", bookId);
							return new BookNotFoundException("Book not found for id=" + bookId);
						});
		return toBookResponse(book);
	}

	public BookResponse createNewBook(
					BookRequest bookRequest,
					User user
	) {
		var book = toBook(bookRequest);
		//TODO: book.setBookCover("");
		book.setUser(user);
		log.info("Saving new book with id = {}", book.getId());
		var savedBook = _bookRepository.save(book);
		return toBookResponse(savedBook);
	}

	public BookResponse updateBook(
					UUID bookId,
					@NonNull BookUpdateRequest bookRequest,
					@NonNull User user
	) {
		var book = _bookRepository.findById(bookId)
						.orElseThrow(() -> {
							log.debug("Book not found for id={}", bookId);
							return new BookNotFoundException("Book not found for id=" + bookId);
						});
		validateAccessToResource(book.getUser().getId(), user.getId());
		log.info("Updating changed fields of book...");
		if (bookRequest.genre() != null) {
			var validGenre = isValidLength(bookRequest.genre(), 2, 20);
			if (validGenre && !book.getGenre().equals(bookRequest.genre())) {
				book.setGenre(bookRequest.genre());
			}
		}

		if (bookRequest.synopsis() != null) {
			var validSynopsis = isValidLength(bookRequest.synopsis(), 20, 255);
			if (validSynopsis && !book.getSynopsis().equals(bookRequest.synopsis())) {
				book.setSynopsis(bookRequest.synopsis());
			}
		}
		// TODO: Book cover
		if (bookRequest.isArchived() != null) {
			if (book.getIsArchived() != bookRequest.isArchived()) {
				book.setIsArchived(bookRequest.isArchived());
			}
		}

		if (bookRequest.isShareable() != null) {
			if (book.getIsShareable() != bookRequest.isShareable()) {
				book.setIsShareable(bookRequest.isShareable());
			}
		}
		log.info("Updating book completed. Saving to database...");
		var updatedBook = _bookRepository.save(book);
		return toBookResponse(updatedBook);
	}

	public UUID deleteBook(
					UUID bookId,
					@NonNull User user
	) {
		var book = _bookRepository.findById(bookId)
						.orElseThrow(() -> {
							log.debug("Book not found for id={}", bookId);
							return new BookNotFoundException("Book not found for id=" + bookId);
						});
		validateAccessToResource(book.getUser().getId(), user.getId());
		log.info("Deleting book with id = {}", book.getId());
		_bookRepository.delete(book);
		return book.getId();
	}

	public PagedResponse<BookResponse> findAllBooksByOwner(
					Integer pageNumber,
					Integer pageSize,
					@NonNull User user
	) {
		log.info("Compiling paged request...");
		var pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
		var spec = Specification.where(withUserId(user.getId()));
		log.info("Fetching all the books for paged response, pageNumber={}, pageSize={}", pageNumber, pageSize);
		var books = _bookRepository.findAll(spec, pageable);
		return toPagedBookResponse(books);
	}


}
// TODO: Toggle book's sharing status and archive status