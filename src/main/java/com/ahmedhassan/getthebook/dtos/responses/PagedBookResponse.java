package com.ahmedhassan.getthebook.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;

// This class only exists to resolve the generic type problem for OpenApi documentation problem.
@Schema(description = "Paged response containing books")
public class PagedBookResponse extends PagedResponse<BookResponse> {}