package com.ahmedhassan.getthebook.audit;

import java.util.UUID;

public final class SystemAuditor {
	private SystemAuditor() {}

	public static final UUID SYSTEM_USER_ID =
					UUID.fromString("00000000-0000-0000-0000-000000000001");
}