package com.tuempresa.correspondencia.repository;
import com.tuempresa.correspondencia.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnexoRepository extends JpaRepository<Anexo, Long> {
    List<Anexo> findByRadicadoId(Long radicadoId);
}