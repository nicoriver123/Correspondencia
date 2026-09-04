package com.tuempresa.correspondencia.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder
public class PqrsResponse {
    private Long id;
    private String numeroRadicado;
    private String tipoPqrs;
    private String categoria;
    private String asunto;
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
    private String descripcion;
    private Long radicadoId;

}