package com.ahmedhassan.getthebook.security.services;

import com.ahmedhassan.getthebook.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.ahmedhassan.getthebook.utils.Utils.maskEmail;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
		log.info("Loading user by username: {}", maskEmail(username));
		return this.userRepository.findUserByEmail(username)
						.orElseThrow(() -> {
							log.debug("User not found with username: {}", maskEmail(username));
							return new UsernameNotFoundException("User not found with username: " + username);
						});
	}
}