package com.tuempresa.correspondencia.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TerceroRequest {
    @NotBlank private String nombreRazonSocial;
    @NotBlank private String numeroIdentificacion;
    @NotBlank private String tipoPersona;
    private String email;
    private String telefono;
    private String direccion;
}