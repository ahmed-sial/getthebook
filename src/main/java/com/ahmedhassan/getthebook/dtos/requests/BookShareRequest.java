package com.ahmedhassan.getthebook.dtos.requests;

import java.util.UUID;

import lombok.Builder;

@Builder
public record BookShareRequest(
  UUID bookId
) {}
