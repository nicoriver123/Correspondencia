package com.tuempresa.correspondencia.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dependencias")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Dependencia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String codigo;

    @ManyToOne
    @JoinColumn(name = "dependencia_padre_id")
    private Dependencia dependenciaPadre;

    @ManyToOne
    @JoinColumn(name = "jefe_id")
    private Usuario jefe;
}