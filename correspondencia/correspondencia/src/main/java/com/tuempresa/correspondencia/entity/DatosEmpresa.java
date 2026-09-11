package com.tuempresa.correspondencia.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "datos_empresa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DatosEmpresa {
    @Id
    private Long id; // siempre 1: solo existe un registro (patrón singleton)

    private String nit;

    @Column(name = "razon_social")
    private String razonSocial;

    @Column(name = "nombre_comercial")
    private String nombreComercial;

    @Column(name = "representante_legal")
    private String representanteLegal;

    private String direccion;
    private String telefono;
    private String email;

    @Column(name = "sitio_web")
    private String sitioWeb;

    @Column(name = "logo_ruta")
    private String logoRuta; // referencia interna (vía StorageService), no una URL pública

    @Column(name = "logo_tipo_mime")
    private String logoTipoMime;
}