package com.ahmedhassan.getthebook.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "applicationAuditAware")
public class JpaAuditingConfig {}