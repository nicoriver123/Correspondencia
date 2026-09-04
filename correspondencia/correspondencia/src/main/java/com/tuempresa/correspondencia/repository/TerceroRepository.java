package com.tuempresa.correspondencia.repository;
import com.tuempresa.correspondencia.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TerceroRepository extends JpaRepository<Tercero, Long> {
    Optional<Tercero> findByNumeroIdentificacion(String numeroIdentificacion);
}