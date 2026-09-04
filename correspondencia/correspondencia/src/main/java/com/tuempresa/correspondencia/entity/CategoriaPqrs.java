package com.tuempresa.correspondencia.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categorias_pqrs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoriaPqrs {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String nombre;
}