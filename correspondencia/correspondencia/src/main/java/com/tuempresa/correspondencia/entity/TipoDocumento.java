package com.tuempresa.correspondencia.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipos_documento")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TipoDocumento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String nombre;
}