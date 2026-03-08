package com.ahmedhassan.getthebook.repositories;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public abstract class BaseRepositoryTest {}