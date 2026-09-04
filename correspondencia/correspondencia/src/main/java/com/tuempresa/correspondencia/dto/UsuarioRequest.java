package com.tuempresa.correspondencia.dto;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UsuarioRequest {
    @NotBlank private String nombre;
    @NotBlank @Email private String email;
    @NotBlank private String password;
    @NotNull private Long rolId;
    private Long dependenciaId;
}