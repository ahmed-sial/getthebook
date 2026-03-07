package com.ahmedhassan.getthebook.mappers;

import com.ahmedhassan.getthebook.dtos.responses.LoginResponse;
import com.ahmedhassan.getthebook.dtos.responses.RegisterResponse;
import com.ahmedhassan.getthebook.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

@Slf4j
public class UserMapper {
	public static RegisterResponse userEntityToRegisterResponse(@NonNull User user) {
		log.info("Mapping user entity to registerResponse via UserMapper.userEntityToRegisterResponse");
		return RegisterResponse
						.builder()
						.id(user.getId())
						.firstName(user.getFirstName())
						.lastName(user.getLastName())
						.email(user.getEmail())
						.build();
	}
	public static LoginResponse userEntityToLoginResponse(@NonNull User user, @NonNull String jwt) {
		log.info("Mapping user entity to loginResponse via UserMapper.userEntityToLoginResponse");
		return LoginResponse
						.builder()
						.id(user.getId())
						.firstName(user.getFirstName())
						.lastName(user.getLastName())
						.email(user.getEmail())
						.token(jwt)
						.build();
	}
}