package com.ahmedhassan.getthebook.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "PagedResponse", description = "Pagination details of response")
public class PagedResponse<T> {

	@Schema(description = "Actual content of paginated response")
	private List<T> content;

	@Schema(description = "Current page number", example = "0")
	private Integer pageNumber;

	@Schema(description = "Current page size", example = "10")
	private Integer pageSize;

	@Schema(description = "Total elements in the paginated response", example = "100")
	private Long totalElements;

	@Schema(description = "Total pages in paginated response", example = "10")
	private Integer totalPages;

	@Schema(description = "Check if current page is first page of paginated response", example = "true")
	private Boolean isFirstPage;

	@Schema(description = "Check if current page is last page of paginated response", example = "false")
	private Boolean isLastPage;
}