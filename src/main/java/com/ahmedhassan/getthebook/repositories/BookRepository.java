package com.ahmedhassan.getthebook.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ahmedhassan.getthebook.entities.Book;

public interface BookRepository extends JpaRepository<Book, UUID>, JpaSpecificationExecutor<Book> {
  Boolean existsByUserIdAndId(UUID userId, UUID bookId);
}