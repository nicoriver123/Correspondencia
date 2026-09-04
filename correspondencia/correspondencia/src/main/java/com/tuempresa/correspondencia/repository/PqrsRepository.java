package com.tuempresa.correspondencia.repository;

import com.tuempresa.correspondencia.entity.Pqrs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PqrsRepository extends JpaRepository<Pqrs, Long>, JpaSpecificationExecutor<Pqrs> {

    Optional<Pqrs> findByRadicadoId(Long radicadoId);

    // ✅ PQRS vencidas (hoy > fecha_limite Y estado != RESPONDIDO/CERRADO)
    @Query("SELECT p FROM Pqrs p WHERE p.fechaLimiteRespuesta < CURRENT_DATE " +
            "AND p.radicado.estado NOT IN ('RESPONDIDO','CERRADO')")
    List<Pqrs> findVencidos();

    // ✅ PQRS próximas a vencer (fecha_limite entre hoy y fechaHoy+3)
    @Query("SELECT p FROM Pqrs p WHERE p.fechaLimiteRespuesta BETWEEN CURRENT_DATE AND :fechaHasta " +
            "AND p.radicado.estado NOT IN ('RESPONDIDO','CERRADO')")
    List<Pqrs> findProximosAVencer(@Param("fechaHasta") LocalDate fechaHasta);
}