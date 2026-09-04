package com.tuempresa.correspondencia.dto;
import lombok.*;
@Data @Builder
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private String dependencia;
    private Boolean estado;
}