package com.tuempresa.correspondencia.controller;

import com.tuempresa.correspondencia.dto.DatosEmpresaRequest;
import com.tuempresa.correspondencia.entity.DatosEmpresa;
import com.tuempresa.correspondencia.service.DatosEmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/empresa")
@RequiredArgsConstructor
public class DatosEmpresaController {
    private final DatosEmpresaService service;

    // Público: el portal ciudadano y el panel interno lo necesitan para pintar
    // el encabezado/pie de página con la marca real de la entidad.
    @GetMapping
    public ResponseEntity<DatosEmpresa> obtener() {
        return ResponseEntity.ok(service.obtener());
    }

    @GetMapping("/logo")
    public ResponseEntity<Resource> logo() throws IOException {
        Resource recurso = service.cargarLogo();
        DatosEmpresa d = service.obtener();
        MediaType tipo = d.getLogoTipoMime() != null
                ? MediaType.parseMediaType(d.getLogoTipoMime())
                : MediaType.IMAGE_PNG;
        return ResponseEntity.ok().contentType(tipo).body(recurso);
    }

    @PutMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DatosEmpresa> actualizar(
            @RequestPart("datos") @Valid DatosEmpresaRequest req,
            @RequestPart(value = "logo", required = false) MultipartFile logo) throws IOException {
        return ResponseEntity.ok(service.actualizar(req, logo));
    }
}