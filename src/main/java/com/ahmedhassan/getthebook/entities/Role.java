package com.ahmedhassan.getthebook.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class Role extends BaseEntity {
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