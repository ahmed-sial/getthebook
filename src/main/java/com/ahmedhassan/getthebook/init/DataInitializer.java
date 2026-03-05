package com.ahmedhassan.getthebook.init;

import com.ahmedhassan.getthebook.audit.SystemAuditor;
import com.ahmedhassan.getthebook.entities.Role;
import com.ahmedhassan.getthebook.entities.User;
import com.ahmedhassan.getthebook.enums.UserRoles;
import com.ahmedhassan.getthebook.repositories.RoleRepository;
import com.ahmedhassan.getthebook.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

	private final RoleRepository _roleRepository;
	private final UserRepository _userRepository;
	private final PasswordEncoder _passwordEncoder;

	@Override
	@Transactional
	public void run(String @NonNull ... args) {
		Role userRole = createRoleIfAbsent(UserRoles.USER.name());
		Role adminRole = createRoleIfAbsent(UserRoles.ADMIN.name());
		Role systemRole = createRoleIfAbsent(UserRoles.SYSTEM.name());

		if (!_userRepository.existsById(SystemAuditor.SYSTEM_USER_ID)) {
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
			_userRepository.save(systemUser);
			System.out.println("System user created.");
		}
	}

	private Role createRoleIfAbsent(String name) {
		return _roleRepository.findRoleByName(name).orElseGet(() -> {
			System.out.println("Role " + name + " created.");
			return _roleRepository.save(Role.builder().name(name).build());
		});
	}
}