package com.tuempresa.correspondencia.repository;
import com.tuempresa.correspondencia.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistorialTrazabilidadRepository extends JpaRepository<HistorialTrazabilidad, Long> {
    List<HistorialTrazabilidad> findByRadicadoIdOrderByFechaAsc(Long radicadoId);
}