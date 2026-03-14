package com.ahmedhassan.getthebook.entities;

import com.ahmedhassan.getthebook.enums.BookAppealStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class BookShareAppeal extends BaseEntity {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "appeal_by", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private BookAppealStatus status;
	@Column(nullable = false)
	private Instant bookShareAppealedAt;
	private Instant bookShareAppealApprovedAt;
}