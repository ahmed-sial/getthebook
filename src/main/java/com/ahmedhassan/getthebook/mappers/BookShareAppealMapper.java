package com.ahmedhassan.getthebook.mappers;

import com.ahmedhassan.getthebook.dtos.responses.BookShareAppealResponse;
import com.ahmedhassan.getthebook.dtos.responses.PagedResponse;
import com.ahmedhassan.getthebook.entities.BookShareAppeal;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;

@Slf4j
public class BookShareAppealMapper {
	public static BookShareAppealResponse toBookShareAppealResponse(BookShareAppeal bookShareAppeal) {
		log.info("Converting BookShareAppeal to BookShareAppealResponse");
		return BookShareAppealResponse
						.builder()
						.appealId(bookShareAppeal.getId())
						.bookId(bookShareAppeal.getBook().getId())
						.status(bookShareAppeal.getStatus())
						.bookShareAppealedAt(bookShareAppeal.getBookShareAppealedAt())
						.bookShareAppealApprovedAt(bookShareAppeal.getBookShareAppealApprovedAt())
						.build();
	}

	public static PagedResponse<BookShareAppealResponse> toBookShareAppealResponse(@NonNull Page<BookShareAppeal> bookShareAppeals) {
		log.info("Converting BookShareAppeals to Paged BookShareAppealResponse");
		var appeals = bookShareAppeals
						.stream()
						.map(BookShareAppealMapper::toBookShareAppealResponse)
						.toList();
		return new PagedResponse<>(
						appeals,
						bookShareAppeals.getNumber(),
						bookShareAppeals.getSize(),
						bookShareAppeals.getTotalElements(),
						bookShareAppeals.getTotalPages(),
						bookShareAppeals.isFirst(),
						bookShareAppeals.isLast()
		);
	}
}