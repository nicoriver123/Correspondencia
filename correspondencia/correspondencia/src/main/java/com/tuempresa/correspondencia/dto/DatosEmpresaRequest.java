package com.tuempresa.correspondencia.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DatosEmpresaRequest {
    @NotBlank private String razonSocial;
    private String nit;
    private String nombreComercial;
    private String representanteLegal;
    private String direccion;
    private String telefono;
    private String email;
    private String sitioWeb;
}