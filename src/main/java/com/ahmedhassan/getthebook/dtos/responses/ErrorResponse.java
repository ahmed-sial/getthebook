package com.ahmedhassan.getthebook.dtos.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@Builder
@Schema(name = "ErrorResponse", description = "Standard error response")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse (
				@Schema(description = "Timestamp at which error occurred", example = "2020-03-06T16:57:05.018522213Z")
				Instant timeStamp,
				@Schema(description = "Status code of error occurred", example = "400")
				Integer status,
				@Schema(description = "Error reason phrase", example = "Bad Request")
				String error,
				@Schema(description = "Error message or description")
				String message,
				@Schema(description = "URI where error occurred")
				String path,
				@Schema(description = "Optional error details", example = "{\"field\": \"must not be blank\"}")
				Map<String, String> details
) {}