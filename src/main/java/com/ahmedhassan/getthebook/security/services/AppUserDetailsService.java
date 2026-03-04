package com.ahmedhassan.getthebook.security.services;

import com.ahmedhassan.getthebook.repositories.UserRepository;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
		return this.userRepository.findUserByEmail(username)
						.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
	}
}