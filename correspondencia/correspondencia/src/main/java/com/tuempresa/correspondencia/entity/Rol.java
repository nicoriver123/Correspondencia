package com.tuempresa.correspondencia.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Rol {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre; // ADMIN, FUNCIONARIO, JEFE_DEPENDENCIA, CIUDADANO

    private String descripcion;
}