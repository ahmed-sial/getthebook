package com.ahmedhassan.getthebook.dtos.responses;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;

@Builder
public record BookShareResponse(
  UUID id,
  UUID sharedTo,
  UUID bookId,
  Instant sharedAt,
  Instant expiresAt
) {}
