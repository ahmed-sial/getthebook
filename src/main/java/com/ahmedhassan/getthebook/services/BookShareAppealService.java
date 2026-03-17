package com.ahmedhassan.getthebook.services;

import static com.ahmedhassan.getthebook.mappers.BookShareAppealMapper.toBookShareAppealResponse;
import static com.ahmedhassan.getthebook.specifications.BookShareAppealSpecification.withUserId;

import java.time.Instant;
import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.ahmedhassan.getthebook.dtos.requests.BookShareAppealRequest;
import com.ahmedhassan.getthebook.dtos.requests.BookShareRequest;
import com.ahmedhassan.getthebook.dtos.responses.BookShareAppealResponse;
import com.ahmedhassan.getthebook.dtos.responses.BookShareResponse;
import com.ahmedhassan.getthebook.dtos.responses.PagedResponse;
import com.ahmedhassan.getthebook.entities.Book;
import com.ahmedhassan.getthebook.entities.BookShare;
import com.ahmedhassan.getthebook.entities.BookShareAppeal;
import com.ahmedhassan.getthebook.entities.User;
import com.ahmedhassan.getthebook.enums.BookAppealStatus;
import com.ahmedhassan.getthebook.exceptions.BookAlreadyBorrowedException;
import com.ahmedhassan.getthebook.exceptions.BookArchivedException;
import com.ahmedhassan.getthebook.exceptions.BookNotFoundException;
import com.ahmedhassan.getthebook.exceptions.BookNotShareableException;
import com.ahmedhassan.getthebook.exceptions.BookShareAppealAlreadyApproved;
import com.ahmedhassan.getthebook.exceptions.BookShareAppealAlreadyExistsException;
import com.ahmedhassan.getthebook.exceptions.BookShareAppealNotFoundException;
import com.ahmedhassan.getthebook.mappers.BookShareMapper;
import com.ahmedhassan.getthebook.repositories.BookRepository;
import com.ahmedhassan.getthebook.repositories.BookShareAppealRepository;
import com.ahmedhassan.getthebook.repositories.BookShareRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookShareAppealService {
	private final BookShareAppealRepository _bookShareAppealRepository;
	private final BookRepository _bookRepository;
	private final BookShareRepository _bookShareRepository;
	private final BookShareService _bookShareService;

	public BookShareAppealResponse createNewBookShareAppeal(
			@NonNull BookShareAppealRequest appealRequest,
			@NonNull User user) {
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

	public PagedResponse<BookShareAppealResponse> fetchCurrentUserBookShareAppeal(
			Integer pageNumber,
			Integer pageSize,
			@NonNull User user) {
		log.info("Compiling paged request...");
		var pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
		log.info("Compiling specifications...");
		var spec = Specification.where(withUserId(user.getId()));
		log.info("Fetching all the appeals for paged response, pageNumber={}, pageSize={}", pageNumber, pageSize);
		var response = _bookShareAppealRepository.findAll(spec, pageable);
		return toBookShareAppealResponse(response);
	}

	@PreAuthorize("hasPermission(#appealId, 'BookShareAppeal', 'READ')")
	public BookShareAppealResponse fetchSingleBookShareAppeal(
			UUID appealId) {
		var appeal = _bookShareAppealRepository.findById(appealId)
				.orElseThrow(() -> new BookShareAppealNotFoundException("Appeal not found with id=" + appealId));
		return toBookShareAppealResponse(appeal);
	}

	@PreAuthorize("hasPermission(#appealId, 'BookShareAppeal', 'DELETE')")
	public UUID deleteBookShareAppeal(
			UUID appealId) {
		log.info("Fetching book share appeal with id={}", appealId);
		var appeal = _bookShareAppealRepository.findById(appealId)
				.orElseThrow(() -> {
					log.debug("Book share appeal not found with id={}", appealId);
					return new BookShareAppealNotFoundException("Book share appeal not found with id=" + appealId);
				});
		log.info("Deleting book share appeal with id={}", appealId);
		_bookShareAppealRepository.delete(appeal);
		return appealId;
	}

	@PreAuthorize("hasPermission(#appealId, 'BookShareAppeal', 'APPROVE')")
	public BookShareResponse approveBookShareAppeal(
			UUID appealId) {
		log.info("Fetching book share appeal with id={}", appealId);
		var appeal = _bookShareAppealRepository.findById(appealId)
				.orElseThrow(() -> {
					log.debug("Book share appeal not found with id={}", appealId);
					return new BookShareAppealNotFoundException("Book share appeal not found with id=" + appealId);
				});
		if (appeal.getBookShareAppealApprovedAt() != null) {
			log.debug("Book share appeal already approved with id={}", appealId);
			throw new BookShareAppealAlreadyApproved(
					"Book share appeal is already approved at" + appeal.getBookShareAppealApprovedAt());
		}
		log.info("Approving book share appeal with id={}", appealId);
		appeal.setBookShareAppealApprovedAt(Instant.now());
		appeal.setStatus(BookAppealStatus.APPROVED);
		log.info("Saving book share appeal with id={}", appealId);
		_bookShareAppealRepository.save(appeal);
		log.info("Saving book share record for approved appeal with id={}", appealId);
		return _bookShareService.createNewBookShareRecord(BookShareRequest.builder().bookId(appeal.getBook().getId()).build(), appeal.getDays(), appeal.getUser());
	}

	@PreAuthorize("hasPermission(#appealId, 'BookShareAppeal', 'REJECT')")
	public BookShareAppealResponse rejectBookShareAppeal(
			UUID appealId) {
		log.info("Fetching book share appeal with id={}", appealId);
		var appeal = _bookShareAppealRepository.findById(appealId)
				.orElseThrow(() -> {
					log.debug("Book share appeal not found with id={}", appealId);
					return new BookShareAppealNotFoundException("Book share appeal not found with id=" + appealId);
				});
		if (appeal.getBookShareAppealApprovedAt() != null) {
			log.debug("Book share appeal already approved with id={}", appealId);
			throw new BookShareAppealAlreadyApproved(
					"Book share appeal is already approved at " + appeal.getBookShareAppealApprovedAt()
							+ ". You can't reject already approved appeal");
		}
		log.info("Rejecting book share appeal with id={}", appealId);
		appeal.setStatus(BookAppealStatus.REJECTED);
		log.info("Saving book share appeal with id={}", appealId);
		var updatedAppeal = _bookShareAppealRepository.save(appeal);
		return toBookShareAppealResponse(updatedAppeal);
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
			throw new BookShareAppealAlreadyExistsException(
					"Book with id=" + book.getId() + " is already appealed for borrowing.");
		}
	}

	private Boolean activeBorrowRecordExists(@NonNull UUID bookId) {
		return _bookShareRepository.existsByBookIdAndExpiresAtIsNull(bookId);
	}

	private Boolean activeAppealExists(@NonNull UUID userId, @NonNull UUID bookId) {
		return _bookShareAppealRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, BookAppealStatus.PENDING);
	}

}