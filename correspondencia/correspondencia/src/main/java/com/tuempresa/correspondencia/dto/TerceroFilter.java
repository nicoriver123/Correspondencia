package com.tuempresa.correspondencia.dto;

import lombok.Data;

@Data
public class TerceroFilter {
    private String textoLibre; // busca en nombre, identificación o correo
    private Boolean estado;
}