package com.tuempresa.correspondencia.controller;

import com.tuempresa.correspondencia.dto.DependenciaRequest;
import com.tuempresa.correspondencia.entity.Dependencia;
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

    @GetMapping
    public ResponseEntity<List<Dependencia>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Dependencia> crear(@Valid @RequestBody DependenciaRequest req) {
        return ResponseEntity.status(201).body(service.crear(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Dependencia> actualizar(@PathVariable Long id,
                                                  @Valid @RequestBody DependenciaRequest req) {
        return ResponseEntity.ok(service.actualizar(id, req));
    }
}