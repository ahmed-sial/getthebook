package com.ahmedhassan.getthebook.repositories;

import com.ahmedhassan.getthebook.entities.BookShareAppeal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookShareAppealRepository extends JpaRepository<BookShareAppeal, UUID> {
}