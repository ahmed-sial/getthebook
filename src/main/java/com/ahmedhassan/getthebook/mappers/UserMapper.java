package com.ahmedhassan.getthebook.mappers;

import com.ahmedhassan.getthebook.dtos.responses.RegisterResponse;
import com.ahmedhassan.getthebook.entities.User;
import org.jspecify.annotations.NonNull;

public class UserMapper {
	public static RegisterResponse userEntityToRegisterResponse(@NonNull User user) {
		return RegisterResponse
						.builder()
						.firstName(user.getFirstName())
						.lastName(user.getLastName())
						.email(user.getEmail())
						.build();
	}
}