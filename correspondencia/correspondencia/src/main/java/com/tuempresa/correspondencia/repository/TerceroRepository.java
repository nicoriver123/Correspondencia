package com.tuempresa.correspondencia.repository;

import com.tuempresa.correspondencia.entity.Tercero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TerceroRepository extends JpaRepository<Tercero, Long>, JpaSpecificationExecutor<Tercero> {
    Optional<Tercero> findByNumeroIdentificacion(String numeroIdentificacion);
}