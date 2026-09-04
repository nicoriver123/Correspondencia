package com.tuempresa.correspondencia.repository;
import com.tuempresa.correspondencia.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface DependenciaRepository extends JpaRepository<Dependencia, Long> {
    List<Dependencia> findByDependenciaPadreIsNull();
    Optional<Dependencia> findByCodigo(String codigo);
}