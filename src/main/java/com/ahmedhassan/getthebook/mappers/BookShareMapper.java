package com.ahmedhassan.getthebook.mappers;

import org.springframework.data.domain.Page;

import com.ahmedhassan.getthebook.dtos.responses.BookShareResponse;
import com.ahmedhassan.getthebook.dtos.responses.PagedResponse;
import com.ahmedhassan.getthebook.entities.BookShare;

public class BookShareMapper {
  public static BookShareResponse tBookShareResponse(BookShare bookShare) {
    return BookShareResponse
    .builder()
    .id(bookShare.getId())
    .bookId(bookShare.getBook().getId())
    .sharedTo(bookShare.getUser().getId())
    .sharedAt(bookShare.getSharedAt())
    .expiresAt(bookShare.getExpiresAt())
    .build(); 
  }

  public static PagedResponse<BookShareResponse> toPagedBookShareResponse(Page<BookShare> bookShares) {
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
