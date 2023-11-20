package com.now.naaga.common;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

public abstract class MySqlContainerTest extends AbstractTest {

    static final MySQLContainer<?> mySqlContainer = new MySQLContainer<>("mysql:8.0.35");

    @DynamicPropertySource
    static void mySqlProperties(final DynamicPropertyRegistry registry) {
        mySqlContainer.start();
        registry.add("spring.datasource.url", mySqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySqlContainer::getUsername);
        registry.add("spring.datasource.password", mySqlContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", mySqlContainer::getDriverClassName);
    }
}
