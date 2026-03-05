package com.ahmedhassan.getthebook.audit;

import com.ahmedhassan.getthebook.entities.User;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ApplicationAuditAware implements AuditorAware<UUID> {

	@Override
	@NullMarked
	public Optional<UUID> getCurrentAuditor() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
			return Optional.of(SystemAuditor.SYSTEM_USER_ID);
		}

		if (auth.getPrincipal() instanceof User user) {
			return Optional.of(user.getId());
		}

		return Optional.of(SystemAuditor.SYSTEM_USER_ID);
	}
}