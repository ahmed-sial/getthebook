package com.ahmedhassan.getthebook.mappers;

import com.ahmedhassan.getthebook.dtos.responses.BookShareAppealResponse;
import com.ahmedhassan.getthebook.entities.BookShareAppeal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookShareAppealMapper {
	public static BookShareAppealResponse toBookShareAppealResponse(BookShareAppeal bookShareAppeal) {
		log.info("Converting BookShareAppeal to BookShareAppealResponse");
		return BookShareAppealResponse
						.builder()
						.appealId(bookShareAppeal.getId())
						.appealBy(bookShareAppeal.getUser().getId())
						.bookId(bookShareAppeal.getBook().getId())
						.status(bookShareAppeal.getStatus())
						.bookShareAppealedAt(bookShareAppeal.getBookShareAppealedAt())
						.bookShareAppealApprovedAt(bookShareAppeal.getBookShareAppealApprovedAt())
						.build();
	}
}