package com.ahmedhassan.getthebook.utils;

import org.jspecify.annotations.NonNull;

public class Utils {
	public static @NonNull String maskEmail(@NonNull String email) {
		return email.replaceAll("(^[^@]{3})[^@]+(@.+)", "$1***$2");
	}
}