package com.ahmedhassan.getthebook.init;

import com.ahmedhassan.getthebook.audit.SystemAuditor;
import com.ahmedhassan.getthebook.entities.Role;
import com.ahmedhassan.getthebook.entities.User;
import com.ahmedhassan.getthebook.enums.UserRoles;
import com.ahmedhassan.getthebook.repositories.RoleRepository;
import com.ahmedhassan.getthebook.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

	private final RoleRepository _roleRepository;
	private final UserRepository _userRepository;
	private final PasswordEncoder _passwordEncoder;

	@Override
	@Transactional
	public void run(String @NonNull ... args) {
		log.info("Initializing data...");
		createRoleIfAbsent(UserRoles.USER.name());
		createRoleIfAbsent(UserRoles.ADMIN.name());
		Role systemRole = createRoleIfAbsent(UserRoles.SYSTEM.name());

		if (!_userRepository.existsById(SystemAuditor.SYSTEM_USER_ID)) {
			log.info("System user doesn't exist. Compiling system user information to save in database");
			User systemUser = User.builder()
							.id(SystemAuditor.SYSTEM_USER_ID)
							.firstName("System")
							.lastName("User")
							.email("system@internal.com")
							.password(_passwordEncoder.encode("system"))
							.role(systemRole)
							.isAccountEnabled(true)
							.isAccountLocked(false)
							.build();
			log.info("System user information compiled");
			log.debug("Saving system user to database");
			_userRepository.save(systemUser);
			log.info("System user created successfully");
		}
	}

	private Role createRoleIfAbsent(String name) {
		log.debug("Finding role for name={}", name);
		return _roleRepository.findRoleByName(name).orElseGet(() -> {
			log.debug("Creating role for name={}", name);
			log.info("Role {} created successfully", name);
			return _roleRepository.save(Role.builder().name(name).build());
		});
	}
}