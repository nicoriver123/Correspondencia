package com.tuempresa.correspondencia.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder
public class RadicadoResponse {
    private Long id;
    private String numeroRadicado;
    private String tipo;
    private LocalDateTime fechaRadicacion;
    private String asunto;
    private String descripcion;
    private String tercero;
    private String dependenciaDestino;
    private String usuarioRadica;
    private String tipoDocumento;
    private String medioRecepcion;
    private String estado;
    private Boolean vencido;
    private Integer diasRestantes;
}