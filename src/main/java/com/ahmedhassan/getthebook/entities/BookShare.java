package com.ahmedhassan.getthebook.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class BookShare extends BaseEntity {
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "shared_to", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@Column(nullable = false)
	private LocalDateTime sharedAt;
	@Column(nullable = false)
	private LocalDateTime expiresAt;
}