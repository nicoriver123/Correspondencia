package com.tuempresa.correspondencia.dto;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder
public class NotificacionResponse {
    private Long id;
    private String titulo;
    private String mensaje;
    private Boolean leida;
    private LocalDateTime fecha;
    private Long pqrsId;
    private Long radicadoId;
}