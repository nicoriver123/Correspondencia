package com.tuempresa.correspondencia.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "dias_festivos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DiaFestivo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private LocalDate fecha;

    private String descripcion;
}