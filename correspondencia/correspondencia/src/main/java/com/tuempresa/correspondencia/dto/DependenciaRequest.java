package com.tuempresa.correspondencia.dto;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DependenciaRequest {
    @NotBlank private String nombre;
    @NotBlank private String codigo;
    private Long dependenciaPadreId;
    private Long jefeId;
}