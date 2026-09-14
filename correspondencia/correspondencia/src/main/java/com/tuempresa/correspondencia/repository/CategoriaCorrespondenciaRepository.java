package com.tuempresa.correspondencia.repository;

import com.tuempresa.correspondencia.entity.CategoriaCorrespondencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CategoriaCorrespondenciaRepository extends JpaRepository<CategoriaCorrespondencia, Long> {
    List<CategoriaCorrespondencia> findByActivoTrue();
    Optional<CategoriaCorrespondencia> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}