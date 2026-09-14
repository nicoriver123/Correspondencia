package com.tuempresa.correspondencia.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PqrsRequest {
    @NotNull private Long tipoCorrespondenciaId; // reemplaza a tipoPqrs + categoriaId
    @NotBlank private String asunto;
    private String descripcion;
    @NotBlank private String nombreTercero;
    @NotBlank private String identificacionTercero;
    private String emailTercero;
    private String telefonoTercero;
    private String direccionTercero;
    @NotBlank private String tipoPersona;
    @NotNull private Long dependenciaDestinoId;
    @NotBlank private String canalEntrada;
}