package com.tuempresa.correspondencia.controller;

import com.tuempresa.correspondencia.dto.DependenciaRequest;
import com.tuempresa.correspondencia.dto.DependenciaResponse;
import com.tuempresa.correspondencia.service.DependenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dependencias")
@RequiredArgsConstructor
public class DependenciaController {
    private final DependenciaService service;

    // Lo usa todo el mundo (público, radicar, filtros de staff): solo activas por defecto.
    @GetMapping
    public ResponseEntity<List<DependenciaResponse>> listar(
            @RequestParam(required = false, defaultValue = "false") boolean incluirInactivas) {
        return ResponseEntity.ok(service.listar(incluirInactivas));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO','JEFE_DEPENDENCIA')")
    public ResponseEntity<DependenciaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DependenciaResponse> crear(@Valid @RequestBody DependenciaRequest req) {
        return ResponseEntity.status(201).body(service.crear(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DependenciaResponse> actualizar(@PathVariable Long id,
                                                          @Valid @RequestBody DependenciaRequest req) {
        return ResponseEntity.ok(service.actualizar(id, req));
    }

    // "Eliminar" = desactivar. No se borra: está enlazada a usuarios y radicados.
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestParam boolean activo) {
        service.cambiarEstado(id, activo);
        return ResponseEntity.noContent().build();
    }
}