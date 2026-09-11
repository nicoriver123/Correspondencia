package com.tuempresa.correspondencia.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notificacion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Builder.Default
    private Boolean leida = false;

    private LocalDateTime fecha;

    // A qué registro lleva el clic en la notificación. pqrsId cuando es sobre
    // una PQRS puntual (la mayoría de los casos); radicadoId cuando es sobre
    // un radicado general que puede no tener PQRS asociada. Ambos nullable:
    // una notificación vieja o genérica puede no tener ninguno de los dos.
    private Long pqrsId;

    private Long radicadoId;

    @PrePersist
    public void prePersist() { this.fecha = LocalDateTime.now(); }
}