package com.ahmedhassan.getthebook.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	@Column(nullable = false, unique = true)
	private String name;

	// Relationship
	@OneToMany(
					mappedBy = "role",
					fetch = FetchType.LAZY
	)
	@JsonIgnore
	private List<User> users;
	//
}