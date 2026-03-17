package com.ahmedhassan.getthebook.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ahmedhassan.getthebook.entities.Role;

public interface RoleRepository extends JpaRepository<Role, UUID> {
	Optional<Role> findRoleByName(String name);

	boolean existsRoleByName(String name);
}