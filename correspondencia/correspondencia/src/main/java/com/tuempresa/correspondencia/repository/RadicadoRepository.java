package com.tuempresa.correspondencia.repository;
import com.tuempresa.correspondencia.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface RadicadoRepository extends JpaRepository<Radicado, Long>,
        JpaSpecificationExecutor<Radicado> {
    Optional<Radicado> findByNumeroRadicado(String numero);

    @Query("SELECT MAX(r.numeroRadicado) FROM Radicado r WHERE r.numeroRadicado LIKE :prefijo%")
    Optional<String> findUltimoNumeroPorPrefijo(String prefijo);

    Page<Radicado> findByDependenciaDestinoId(Long depId, Pageable p);
}
