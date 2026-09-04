package com.tuempresa.correspondencia.dto;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RespuestaRequest {
    @NotBlank private String contenido;
    private String medioEnvio;
    private Long anexoId;
}