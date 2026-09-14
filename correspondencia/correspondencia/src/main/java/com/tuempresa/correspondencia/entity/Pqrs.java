package com.tuempresa.correspondencia.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "pqrs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Pqrs {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "radicado_id", nullable = false, unique = true)
    private Radicado radicado;

    // La clasificación (tipo/categoría) y el plazo legal ahora viven en
    // radicado.tipoCorrespondencia — ya no se duplican aquí.

    @Column(name = "fecha_limite_respuesta")
    private LocalDate fechaLimiteRespuesta;

    @Column(name = "dias_habiles_termino")
    private Integer diasHabilesTermino;

    @ManyToOne
    @JoinColumn(name = "usuario_asignado_id")
    private Usuario usuarioAsignado;

    @Column(name = "canal_entrada")
    private String canalEntrada;
}