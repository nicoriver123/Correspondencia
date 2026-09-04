package com.tuempresa.correspondencia.dto;
import lombok.*;

@Data @AllArgsConstructor
public class ReporteConteo {
    private String grupo;
    private Long total;
}