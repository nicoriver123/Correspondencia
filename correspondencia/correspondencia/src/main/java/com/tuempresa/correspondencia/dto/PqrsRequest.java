package com.tuempresa.correspondencia.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PqrsRequest {
    @NotBlank private String tipoPqrs;       // PETICION, QUEJA, RECLAMO, SUGERENCIA, DENUNCIA, FELICITACION
    private Long categoriaId;
    @NotBlank private String asunto;
    private String descripcion;
    @NotBlank private String nombreTercero;
    @NotBlank private String identificacionTercero;
    private String emailTercero;
    private String telefonoTercero;
    private String direccionTercero;
    @NotBlank private String tipoPersona;    // NATURAL / JURIDICA
    @NotNull private Long dependenciaDestinoId;
    @NotBlank private String canalEntrada;   // WEB, PRESENCIAL, TELEFONICO
}