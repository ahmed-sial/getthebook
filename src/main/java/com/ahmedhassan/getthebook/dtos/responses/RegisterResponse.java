package com.ahmedhassan.getthebook.dtos.responses;

import lombok.Builder;

@Builder
public record RegisterResponse(
				String firstName,
				String lastName,
				String email
) {
}