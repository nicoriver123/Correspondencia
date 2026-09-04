package com.tuempresa.correspondencia.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "terceros")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Tercero {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_persona", nullable = false)
    private String tipoPersona; // NATURAL / JURIDICA

    @Column(name = "nombre_razon_social", nullable = false)
    private String nombreRazonSocial;

    @Column(name = "numero_identificacion", unique = true)
    private String numeroIdentificacion;

    private String direccion;
    private String telefono;
    private String email;
}