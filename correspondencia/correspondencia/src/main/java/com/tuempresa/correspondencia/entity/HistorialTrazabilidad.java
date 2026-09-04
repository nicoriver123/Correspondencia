package com.tuempresa.correspondencia.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_trazabilidad")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HistorialTrazabilidad {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "radicado_id", nullable = false)
    private Radicado radicado;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String accion;

    @ManyToOne
    @JoinColumn(name = "dependencia_origen_id")
    private Dependencia dependenciaOrigen;

    @ManyToOne
    @JoinColumn(name = "dependencia_destino_id")
    private Dependencia dependenciaDestino;

    @Column(columnDefinition = "TEXT")
    private String observacion;

    private LocalDateTime fecha;

    @PrePersist
    public void prePersist() { this.fecha = LocalDateTime.now(); }
}