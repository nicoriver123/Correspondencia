package com.tuempresa.correspondencia.repository;
import com.tuempresa.correspondencia.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface DiaFestivoRepository extends JpaRepository<DiaFestivo, Long> {
    List<DiaFestivo> findByFechaBetween(LocalDate desde, LocalDate hasta);
}
