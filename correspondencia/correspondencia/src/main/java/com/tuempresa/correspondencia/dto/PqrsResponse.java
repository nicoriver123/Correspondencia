package com.tuempresa.correspondencia.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder
public class PqrsResponse {
    private Long id;
    private Long radicadoId;
    private String numeroRadicado;
    private String categoriaCorrespondencia; // ej. "Solicitud"
    private String tipoCorrespondencia;      // ej. "Petición externa" (el subtipo)
    private String asunto;
    private String descripcion;
    private String tercero;
    private String dependenciaDestino;
    private String usuarioAsignado;
    private String estado;
    private LocalDate fechaLimiteRespuesta;
    private Integer diasHabilesTermino;
    private Boolean vencido;
    private Integer diasRestantes;
    private String canalEntrada;
    private LocalDateTime fechaRadicacion;
}