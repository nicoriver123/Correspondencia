package com.tuempresa.correspondencia.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TipoCorrespondenciaRequest {
    @NotBlank private String nombre;
    @NotBlank private String naturaleza; // ENTRADA / SALIDA / INTERNO
    private Boolean esSolicitud;
}