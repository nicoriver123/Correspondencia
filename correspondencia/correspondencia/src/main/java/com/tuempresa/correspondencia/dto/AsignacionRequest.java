package com.tuempresa.correspondencia.dto;
import lombok.Data;

@Data
public class AsignacionRequest {
    private Long dependenciaId;
    private Long usuarioId;
    private String observacion;
}
