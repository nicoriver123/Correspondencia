package com.tuempresa.correspondencia.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PqrsFilter {
    private String textoLibre;
    private Long categoriaCorrespondenciaId;
    private Long tipoCorrespondenciaId;
    private String estado;
    private Long dependenciaId;
    private Long usuarioAsignadoId;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private Boolean soloVencidos;
    private Boolean soloProximosAVencer;
}