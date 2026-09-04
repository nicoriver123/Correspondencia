package com.tuempresa.correspondencia.controller;

import com.tuempresa.correspondencia.dto.*;
import com.tuempresa.correspondencia.entity.*;
import com.tuempresa.correspondencia.repository.UsuarioRepository;
import com.tuempresa.correspondencia.service.PdfExportService;
import com.tuempresa.correspondencia.service.RadicadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/radicados")
@RequiredArgsConstructor
public class RadicadoController {
    private final RadicadoService service;
    private final UsuarioRepository userRepo;
    private final PdfExportService pdfService; // <-- ESTA LÍNEA FALTABA

    @PostMapping
    @PreAuthorize("hasAnyRole('FUNCIONARIO','ADMIN','JEFE_DEPENDENCIA')")
    public ResponseEntity<RadicadoResponse> crear(@Valid @RequestBody RadicadoRequest req,
                                                  @AuthenticationPrincipal UserDetails ud) {
        Usuario u = userRepo.findByEmail(ud.getUsername()).orElseThrow();
        return ResponseEntity.status(201).body(service.crear(req, u));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FUNCIONARIO','ADMIN','JEFE_DEPENDENCIA')")
    public ResponseEntity<Page<RadicadoResponse>> listar(
            @org.springframework.web.bind.annotation.ModelAttribute RadicadoFilter filter,
            Pageable p) {
        return ResponseEntity.ok(service.listar(filter, p));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RadicadoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @GetMapping("/numero/{numero}")
    public ResponseEntity<RadicadoResponse> obtenerPorNumero(@PathVariable String numero) {
        return ResponseEntity.ok(service.obtenerPorNumero(numero));
    }

    @PatchMapping("/{id}/asignar")
    @PreAuthorize("hasAnyRole('JEFE_DEPENDENCIA','ADMIN')")
    public ResponseEntity<Void> asignar(@PathVariable Long id,
                                        @RequestBody AsignacionRequest req,
                                        @AuthenticationPrincipal UserDetails ud) {
        Usuario u = userRepo.findByEmail(ud.getUsername()).orElseThrow();
        service.asignar(id, req, u);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('FUNCIONARIO','ADMIN','JEFE_DEPENDENCIA')")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id,
                                              @Valid @RequestBody CambioEstadoRequest req,
                                              @AuthenticationPrincipal UserDetails ud) {
        Usuario u = userRepo.findByEmail(ud.getUsername()).orElseThrow();
        service.cambiarEstado(id, req, u);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialTrazabilidad>> historial(@PathVariable Long id) {
        return ResponseEntity.ok(service.historial(id));
    }

    @GetMapping("/{id}/constancia-pdf")
    public ResponseEntity<byte[]> constanciaPdf(@PathVariable Long id) {
        byte[] pdf = pdfService.generarConstanciaRadicado(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=constancia-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
    @GetMapping("/{id}/respuestas")
    @PreAuthorize("hasAnyRole('FUNCIONARIO','ADMIN','JEFE_DEPENDENCIA')")
    public ResponseEntity<List<RespuestaResponse>> respuestas(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarRespuestas(id));
    }
}