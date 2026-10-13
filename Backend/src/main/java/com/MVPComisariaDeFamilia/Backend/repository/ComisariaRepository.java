package com.MVPComisariaDeFamilia.Backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MVPComisariaDeFamilia.Backend.entity.Comisaria;

public interface ComisariaRepository extends JpaRepository<Comisaria, Long> {

    Optional<Comisaria> findByCodigo(String codigo);

    List<Comisaria> findByActivaTrueOrderByNombreAsc();
}