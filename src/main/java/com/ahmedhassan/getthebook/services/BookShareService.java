package com.ahmedhassan.getthebook.services;

import static com.ahmedhassan.getthebook.mappers.BookShareMapper.tBookShareResponse;
import static com.ahmedhassan.getthebook.mappers.BookShareMapper.toPagedBookShareResponse;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.ahmedhassan.getthebook.dtos.requests.BookShareRequest;
import com.ahmedhassan.getthebook.dtos.responses.BookShareResponse;
import com.ahmedhassan.getthebook.dtos.responses.PagedResponse;
import com.ahmedhassan.getthebook.entities.BookShare;
import com.ahmedhassan.getthebook.entities.User;
import com.ahmedhassan.getthebook.exceptions.BookNotFoundException;
import com.ahmedhassan.getthebook.exceptions.BookShareRecordNotFound;
import com.ahmedhassan.getthebook.repositories.BookRepository;
import com.ahmedhassan.getthebook.repositories.BookShareRepository;
import static com.ahmedhassan.getthebook.specifications.BookShareSpecification.withBookId;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookShareService {
  private final BookShareRepository _bookShareRepository;
  private final BookRepository _bookRepository;

  BookShareResponse createNewBookShareRecord(
          @NonNull BookShareRequest bookShareRequest,
          Integer days,
          User user) {
    log.info("Fetching book details...");
    var book = _bookRepository.findById(bookShareRequest.bookId())
        .orElseThrow(() -> {
          log.debug("Book not found with id {}", bookShareRequest.bookId());
          return new BookNotFoundException("Book not found with id=" + bookShareRequest.bookId());
        });
    log.info("Compiling new book share record information...");
    var rawBookShare = BookShare
        .builder()
        .user(user)
        .book(book)
        .sharedAt(Instant.now())
        .expiresAt(Instant.now().plus(Duration.ofDays(days)))
        .build();
    log.info("Saving book share record information...");
    var bookShare = _bookShareRepository.save(rawBookShare);
    return tBookShareResponse(bookShare);
  }

  public BookShareResponse getSingleBookShareRecord(
    UUID bookShareId
  ) {
    log.info("Fetching book share record information...");
    var record = _bookShareRepository.findById(bookShareId)
    .orElseThrow(() -> {
      log.debug("Book share record not found with id {}", bookShareId);
      return new BookShareRecordNotFound("Book share record not found with id=" + bookShareId);
    });
    return tBookShareResponse(record);
  }

  @PreAuthorize("hasPermission(#bookId, 'BookShare', 'READ')")
  public PagedResponse<BookShareResponse> getAllBookShareRecords(
    Integer pageNumber,
    Integer pageSize,
    UUID bookId
  ) {
    var pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
    var spec = Specification.where(withBookId(bookId));
    log.info("Fetching book share records information...");
    var records = _bookShareRepository.findAll(spec, pageable);
    return toPagedBookShareResponse(records);
  }
}