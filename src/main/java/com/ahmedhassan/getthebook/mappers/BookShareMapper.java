package com.ahmedhassan.getthebook.mappers;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;

import com.ahmedhassan.getthebook.dtos.responses.BookShareResponse;
import com.ahmedhassan.getthebook.dtos.responses.PagedResponse;
import com.ahmedhassan.getthebook.entities.BookShare;

@Slf4j
public class BookShareMapper {
  public static BookShareResponse tBookShareResponse(@NonNull BookShare bookShare) {
    log.info("Converting BookShare to BookShareResponse");
    return BookShareResponse
    .builder()
    .id(bookShare.getId())
    .bookId(bookShare.getBook().getId())
    .sharedTo(bookShare.getUser().getId())
    .sharedAt(bookShare.getSharedAt())
    .expiresAt(bookShare.getExpiresAt())
    .build();
  }

  public static PagedResponse<BookShareResponse> toPagedBookShareResponse(@NonNull Page<BookShare> bookShares) {
    log.info("Converting BookShare to Paged BookShareResponse");
    var res = bookShares.stream().map(BookShareMapper::tBookShareResponse).toList();
    return new PagedResponse<>(
      res,
      bookShares.getNumber(),
      bookShares.getSize(),
      bookShares.getTotalElements(),
      bookShares.getTotalPages(),
      bookShares.isFirst(),
      bookShares.isLast()
    );
  }
}