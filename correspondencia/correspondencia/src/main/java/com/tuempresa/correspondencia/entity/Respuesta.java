package com.tuempresa.correspondencia.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "respuestas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Respuesta {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "radicado_id", nullable = false)
    private Radicado radicado;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(columnDefinition = "TEXT")
    private String contenido;

    @ManyToOne
    @JoinColumn(name = "anexo_id")
    private Anexo anexo;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @Column(name = "medio_envio")
    private String medioEnvio;

    @PrePersist
    public void prePersist() { this.fechaRespuesta = LocalDateTime.now(); }
}