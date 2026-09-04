package com.tuempresa.correspondencia.dto;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CambioEstadoRequest {
    @NotBlank private String nuevoEstado;
    private String observacion;
}