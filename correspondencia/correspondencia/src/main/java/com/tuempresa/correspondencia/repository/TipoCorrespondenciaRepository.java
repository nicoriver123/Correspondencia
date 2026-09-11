package com.tuempresa.correspondencia.repository;

import com.tuempresa.correspondencia.entity.TipoCorrespondencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TipoCorrespondenciaRepository extends JpaRepository<TipoCorrespondencia, Long> {
    List<TipoCorrespondencia> findByActivoTrue();
}