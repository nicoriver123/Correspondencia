package com.tuempresa.correspondencia.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    @Column(nullable = false)
    @Builder.Default
    private Boolean estado = true;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void prePersist() {
        if (fechaRegistro == null) fechaRegistro = LocalDateTime.now();
        if (estado == null) estado = true;
    }
}