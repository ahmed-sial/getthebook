package com.ahmedhassan.getthebook.services;

import com.ahmedhassan.getthebook.dtos.requests.BookShareAppealRequest;
import com.ahmedhassan.getthebook.dtos.responses.BookShareAppealResponse;
import com.ahmedhassan.getthebook.dtos.responses.PagedResponse;
import com.ahmedhassan.getthebook.entities.Book;
import com.ahmedhassan.getthebook.entities.BookShareAppeal;
import com.ahmedhassan.getthebook.entities.User;
import com.ahmedhassan.getthebook.enums.BookAppealStatus;
import com.ahmedhassan.getthebook.exceptions.*;
import com.ahmedhassan.getthebook.repositories.BookRepository;
import com.ahmedhassan.getthebook.repositories.BookShareAppealRepository;
import com.ahmedhassan.getthebook.repositories.BookShareRepository;
import com.ahmedhassan.getthebook.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

import static com.ahmedhassan.getthebook.mappers.BookShareAppealMapper.toBookShareAppealResponse;
import static com.ahmedhassan.getthebook.specifications.BookShareAppealSpecification.withUserId;
import static com.ahmedhassan.getthebook.utils.Utils.validateAccessToResource;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookShareAppealService {
	private final BookShareAppealRepository _bookShareAppealRepository;
	private final BookRepository _bookRepository;
	private final BookShareRepository _bookShareRepository;

	public BookShareAppealResponse createNewBookShareAppeal(
					@NonNull BookShareAppealRequest appealRequest,
					@NonNull User user
	) {
		log.info("Fetching book information...");
		var bookToBorrow = _bookRepository.findById(appealRequest.bookId())
						.orElseThrow(() -> {
							log.debug("Book not found for id={}", appealRequest.bookId());
							return new BookNotFoundException("Book not found with id=" + appealRequest.bookId());
						});

		log.info("Checking business constraint for book share appeal");
		validateBookShareAppeal(user, bookToBorrow);
		log.info("All business constraints resolved for book share appeal. Compiling book share appeal information...");

		var bookShareAppeal = BookShareAppeal
						.builder()
						.book(bookToBorrow)
						.user(user)
						.status(BookAppealStatus.PENDING)
						.bookShareAppealedAt(Instant.now())
						.bookShareAppealApprovedAt(null)
						.build();

		log.info("Saving book share appeal to database");
		var appeal = _bookShareAppealRepository.save(bookShareAppeal);
		return toBookShareAppealResponse(appeal);
	}

	private void validateBookShareAppeal(@NonNull User user, @NonNull Book book) {

		// 1. Not borrowable for owner of book
		if (user.getId().equals(book.getUser().getId())) {
			log.debug("Current logged in user is owner of book with id={}", book.getId());
			throw new BookNotShareableException("You cannot lend book to yourself with id=" + book.getId());
		}

		// 2. Not borrowable if archived
		if (book.getIsArchived()) {
			log.debug("Book archived for book with id={}", book.getId());
			throw new BookArchivedException("Book has been archived with id=" + book.getId());
		}

		// 3. Not borrowable if not shareable
		if (!book.getIsShareable()) {
			log.debug("Book share not applicable for book with id={}", book.getId());
			throw new BookNotShareableException("Book is not shareable with id=" + book.getId());
		}

		// 4. Not borrowable if book is currently borrowed
		if (activeBorrowRecordExists(book.getId())) {
			log.debug("Book already borrowed for user with id={}", user.getId());
			throw new BookAlreadyBorrowedException("Book with id=" + book.getId() + " is already borrowed.");
		}

		// 5. Not borrowable if already appealed for borrowing
		if (activeAppealExists(user.getId(), book.getId())) {
			log.debug("Book already appealed for borrowing for user with id={}", user.getId());
			throw new BookShareAppealAlreadyExistsException("Book with id=" + book.getId() + " is already appealed for borrowing.");
		}
	}

	public PagedResponse<BookShareAppealResponse> fetchCurrentUserBookShareAppeal(
					Integer pageNumber,
					Integer pageSize,
					@NonNull User user
	) {
		log.info("Compiling paged request...");
		var pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
		log.info("Compiling specifications...");
		var spec = Specification.where(withUserId(user.getId()));
		log.info("Fetching all the appeals for paged response, pageNumber={}, pageSize={}", pageNumber, pageSize);
		var response = _bookShareAppealRepository.findAll(spec, pageable);
		return toBookShareAppealResponse(response);
	}

	public BookShareAppealResponse fetchSingleBookShareAppeal(
					UUID appealId,
					@NonNull User user
	) {
		var appeal = _bookShareAppealRepository.findById(appealId)
						.orElseThrow(() -> new BookShareAppealNotFoundException("Appeal not found with id=" + appealId));
		validateAccessToResource(appeal.getUser().getId(), user.getId());
		return toBookShareAppealResponse(appeal);
	}

	private Boolean activeBorrowRecordExists(@NonNull UUID bookId) {
		return _bookShareRepository.existsByBookIdAndExpiresAtIsNull(bookId);
	}
	private Boolean activeAppealExists(@NonNull UUID userId, @NonNull UUID bookId) {
		return _bookShareAppealRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, BookAppealStatus.PENDING);
	}
}