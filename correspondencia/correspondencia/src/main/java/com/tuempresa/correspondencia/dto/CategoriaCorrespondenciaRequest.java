package com.tuempresa.correspondencia.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoriaCorrespondenciaRequest {
    @NotBlank private String nombre;
    private String descripcion;
}