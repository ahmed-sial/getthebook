package com.ahmedhassan.getthebook.services;

import com.ahmedhassan.getthebook.dtos.requests.BookShareAppealRequest;
import com.ahmedhassan.getthebook.dtos.responses.BookShareAppealResponse;
import com.ahmedhassan.getthebook.entities.BookShareAppeal;
import com.ahmedhassan.getthebook.entities.User;
import com.ahmedhassan.getthebook.repositories.BookShareAppealRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookShareAppealService {
	private final BookShareAppealRepository _bookShareAppealRepository;

	public BookShareAppealResponse createNewBookShareAppeal(
					BookShareAppealRequest appealRequest,
					User user
	) {
		// CONTINUE
		return null;
	}
}