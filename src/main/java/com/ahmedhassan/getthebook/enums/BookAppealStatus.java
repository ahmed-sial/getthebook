package com.ahmedhassan.getthebook.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BookAppealStatus {
	PENDING("PENDING"),
	APPROVED("APPROVED"),
	REJECTED("REJECTED")
	;
	private final String status;
	BookAppealStatus(String status) {
		this.status = status;
	}

	@JsonValue // Mark this method's return value for serialization
	public String getStatus() {
		return status.toLowerCase();
	}

	@JsonCreator // Mark this method's return value for deserialization
	public static BookAppealStatus fromString(String status) {
		for (BookAppealStatus item : BookAppealStatus.values()) {
			if (item.toString().equalsIgnoreCase(status)) {
				return item;
			}
		}
		return null;
	}
}