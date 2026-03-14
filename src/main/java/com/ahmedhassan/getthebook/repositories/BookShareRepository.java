package com.ahmedhassan.getthebook.repositories;

import com.ahmedhassan.getthebook.entities.BookShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookShareRepository extends JpaRepository<BookShare, UUID> {
}