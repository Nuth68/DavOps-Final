package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DemoApplicationTests {

    @Autowired
    private Environment env;

    @Autowired
    private DataSource dataSource;

    @Test
    void contextLoads() {
        assertNotNull(env);
    }

    @Test
    void testDatabaseIsH2NotMySQL() {
        String url = env.getProperty("spring.datasource.url");
        assertNotNull(url);
        assertTrue(url.contains("h2"), "Test DB must be H2, got: " + url);
        assertFalse(url.contains("mysql"), "Test DB must not be MySQL");
    }

    @Test
    void canConnectToTestDatabase() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            assertNotNull(conn);
            assertFalse(conn.isClosed());
            DatabaseMetaData meta = conn.getMetaData();
            assertTrue(meta.getDatabaseProductName().contains("H2"),
                "Expected H2 test database but got: " + meta.getDatabaseProductName());
        }
    }
}
