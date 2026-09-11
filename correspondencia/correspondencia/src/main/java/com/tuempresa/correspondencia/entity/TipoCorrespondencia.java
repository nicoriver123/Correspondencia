package com.tuempresa.correspondencia.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipos_correspondencia")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TipoCorrespondencia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre; // ej. "Petición externa", "Oficio de salida", "Memorando interno"

    @Column(nullable = false)
    private String naturaleza; // ENTRADA / SALIDA / INTERNO — misma base que Radicado.tipo

    @Column(name = "es_solicitud", nullable = false)
    @Builder.Default
    private Boolean esSolicitud = false; // true = Solicitud (con plazo, tipo PQRS) / false = Trámite

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
}