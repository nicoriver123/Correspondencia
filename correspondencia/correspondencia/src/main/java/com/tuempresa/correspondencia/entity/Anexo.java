package com.tuempresa.correspondencia.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "anexos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Anexo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "radicado_id", nullable = false)
    private Radicado radicado;

    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @Column(name = "ruta_almacenamiento", nullable = false)
    private String rutaAlmacenamiento;

    @Column(name = "tipo_mime")
    private String tipoMime;

    @Column(name = "tamano_bytes")
    private Long tamanoBytes;

    @ManyToOne
    @JoinColumn(name = "usuario_carga_id")
    private Usuario usuarioCarga;

    @Column(name = "fecha_carga")
    private LocalDateTime fechaCarga;

    @PrePersist
    public void prePersist() { this.fechaCarga = LocalDateTime.now(); }
}