package com.MVPComisariaDeFamilia.Backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

/** Requiere PostgreSQL levantado: docker compose up -d */
@SpringBootTest
class DatabaseConnectionTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void conectaConPostgres() {
        Integer resultado = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertEquals(1, resultado);
    }

    @Test
    void flywayAplicoLaLineaBase() {
        Integer migraciones = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM flyway_schema_history WHERE success = true", Integer.class);
        assertTrue(migraciones != null && migraciones >= 1);
    }
}