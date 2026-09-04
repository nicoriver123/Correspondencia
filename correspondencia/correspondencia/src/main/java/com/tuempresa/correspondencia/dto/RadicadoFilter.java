package com.tuempresa.correspondencia.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RadicadoFilter {
    private String textoLibre;         // busca en asunto, descripción, número
    private String tipo;               // ENTRADA, SALIDA, INTERNO
    private String estado;
    private Long dependenciaId;
    private Long terceroId;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
}