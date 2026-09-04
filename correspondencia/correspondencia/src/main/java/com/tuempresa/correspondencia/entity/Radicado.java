package com.tuempresa.correspondencia.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "radicados")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Radicado {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_radicado", unique = true, nullable = false)
    private String numeroRadicado;

    @Column(nullable = false)
    private String tipo; // ENTRADA, SALIDA, INTERNO

    @Column(name = "fecha_radicacion", nullable = false)
    private LocalDateTime fechaRadicacion;

    @Column(nullable = false)
    private String asunto;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "tercero_id")
    private Tercero tercero;

    @ManyToOne
    @JoinColumn(name = "dependencia_destino_id", nullable = false)
    private Dependencia dependenciaDestino;

    @ManyToOne
    @JoinColumn(name = "usuario_radica_id", nullable = false)
    private Usuario usuarioRadica;

    @ManyToOne
    @JoinColumn(name = "tipo_documento_id")
    private TipoDocumento tipoDocumento;

    @Column(name = "medio_recepcion")
    private String medioRecepcion;

    @Column(nullable = false)
    @Builder.Default
    private String estado = "RADICADO"; // RADICADO, ASIGNADO, EN_TRAMITE, RESPONDIDO, CERRADO

    @PrePersist
    public void prePersist() {
        if (fechaRadicacion == null) fechaRadicacion = LocalDateTime.now();
    }
}