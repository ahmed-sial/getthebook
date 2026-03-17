package com.ahmedhassan.getthebook.repositories;

import com.ahmedhassan.getthebook.entities.BookShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface BookShareRepository extends JpaRepository<BookShare, UUID>, JpaSpecificationExecutor<BookShare> {
	Boolean existsByBookIdAndExpiresAtIsNull(UUID bookId);
}