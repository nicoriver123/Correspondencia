package com.tuempresa.correspondencia.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PqrsFilter {
    private String textoLibre;
    private String tipoPqrs;
    private Long categoriaId;
    private String estado;
    private Long dependenciaId;
    private Long usuarioAsignadoId;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private Boolean soloVencidos;
    private Boolean soloProximosAVencer; // ≤ 3 días
}