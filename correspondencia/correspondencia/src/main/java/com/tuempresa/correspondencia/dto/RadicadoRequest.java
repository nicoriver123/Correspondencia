package com.tuempresa.correspondencia.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RadicadoRequest {
    @NotBlank private String tipo;           // ENTRADA, SALIDA, INTERNO
    @NotBlank private String asunto;
    private String descripcion;
    @NotNull private Long terceroId;
    @NotNull private Long dependenciaDestinoId;
    private Long tipoDocumentoId;
    private String medioRecepcion;
    private Long tipoCorrespondenciaId; // opcional
}