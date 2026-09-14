package com.tuempresa.correspondencia.dto;

import lombok.*;

@Data @Builder
public class DependenciaResponse {
    private Long id;
    private String nombre;
    private String codigo;
    private Long dependenciaPadreId;
    private String dependenciaPadreNombre;
    private Long jefeId;
    private String jefeNombre;
    private Boolean activo;
}