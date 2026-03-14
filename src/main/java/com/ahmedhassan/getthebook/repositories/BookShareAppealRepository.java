package com.ahmedhassan.getthebook.repositories;

import com.ahmedhassan.getthebook.entities.BookShareAppeal;
import com.ahmedhassan.getthebook.enums.BookAppealStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface BookShareAppealRepository extends JpaRepository<BookShareAppeal, UUID>, JpaSpecificationExecutor<BookShareAppeal> {
	Boolean existsByUserIdAndBookIdAndStatus(UUID userId, UUID bookId, BookAppealStatus status);
}