package com.MVPComisariaDeFamilia.Backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.MVPComisariaDeFamilia.Backend.entity.Comisaria;
import com.MVPComisariaDeFamilia.Backend.repository.ComisariaRepository;

/** Requiere PostgreSQL levantado: docker compose up -d */
@SpringBootTest
class ComisariaRepositoryTest {

    @Autowired
    private ComisariaRepository comisariaRepository;

    @Test
    void catalogoInicialTieneLasCuatroComisarias() {
        assertEquals(4, comisariaRepository.findByActivaTrueOrderByNombreAsc().size());
    }

    @Test
    void buscaComisariaPorCodigo() {
        Comisaria comisaria = comisariaRepository.findByCodigo("COM-01").orElseThrow();
        assertTrue(comisaria.isActiva());
        assertTrue(comisaria.getId() != null);
    }

    @Test
    void codigoInexistenteNoDevuelveNada() {
        assertTrue(comisariaRepository.findByCodigo("NO-EXISTE").isEmpty());
    }
}