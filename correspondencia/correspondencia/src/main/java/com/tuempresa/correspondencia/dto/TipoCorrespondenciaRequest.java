package com.tuempresa.correspondencia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TipoCorrespondenciaRequest {
    @NotNull private Long categoriaId;
    @NotBlank private String nombre;
    private String descripcion;
    private Integer diasTermino; // opcional; solo aplica si es una "Solicitud"
}