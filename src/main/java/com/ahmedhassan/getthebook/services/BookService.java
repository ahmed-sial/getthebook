package com.ahmedhassan.getthebook.services;

import com.ahmedhassan.getthebook.dtos.requests.BookRequest;
import com.ahmedhassan.getthebook.dtos.responses.BookResponse;
import com.ahmedhassan.getthebook.dtos.responses.PagedResponse;
import com.ahmedhassan.getthebook.entities.User;
import com.ahmedhassan.getthebook.exceptions.BookNotFoundException;
import com.ahmedhassan.getthebook.mappers.BookMapper;
import com.ahmedhassan.getthebook.repositories.BookRepository;
import com.ahmedhassan.getthebook.specifications.BookSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.ahmedhassan.getthebook.mappers.BookMapper.*;
import static com.ahmedhassan.getthebook.specifications.BookSpecification.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

	private final BookRepository _bookRepository;

	public PagedResponse<BookResponse> findAllBooksExceptCurrentUser(
					int pageNumber,
					int pageSize,
					@NonNull User user
	) {
		log.info("Compiling paged request...");
		var pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt")
						.descending());
		var spec = Specification
						.where(shareable())
						.and(notArchived())
						.and(withoutOwnerId(user.getId()));
		log.info("Fetching all the books for paged response, pageNumber={}, pageSize={}", pageNumber, pageSize);
		var books = _bookRepository.findAll(spec, pageable);
		return toPagedBookResponse(books);
	}

	public BookResponse findSingleBookById(UUID bookId) {
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

}