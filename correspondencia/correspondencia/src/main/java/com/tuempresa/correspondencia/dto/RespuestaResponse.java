package com.tuempresa.correspondencia.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder
public class RespuestaResponse {
    private Long id;
    private String contenido;
    private String medioEnvio;
    private String usuario;       // solo el nombre, no la entidad completa
    private LocalDateTime fechaRespuesta;
}