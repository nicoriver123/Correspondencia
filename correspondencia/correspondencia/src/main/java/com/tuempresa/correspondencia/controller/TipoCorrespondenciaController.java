package com.tuempresa.correspondencia.controller;

import com.tuempresa.correspondencia.dto.TipoCorrespondenciaRequest;
import com.tuempresa.correspondencia.entity.TipoCorrespondencia;
import com.tuempresa.correspondencia.exception.ResourceNotFoundException;
import com.tuempresa.correspondencia.repository.TipoCorrespondenciaRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-correspondencia")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO','JEFE_DEPENDENCIA')")
public class TipoCorrespondenciaController {
    private final TipoCorrespondenciaRepository repo;

    // Todos los de staff pueden verlos (para elegirlos al radicar).
    @GetMapping
    public ResponseEntity<List<TipoCorrespondencia>> listar(
            @RequestParam(required = false, defaultValue = "false") boolean incluirInactivos) {
        return ResponseEntity.ok(incluirInactivos ? repo.findAll() : repo.findByActivoTrue());
    }

    // Crear, editar y (des)activar: solo ADMIN.
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TipoCorrespondencia> crear(@Valid @RequestBody TipoCorrespondenciaRequest req) {
        TipoCorrespondencia t = TipoCorrespondencia.builder()
                .nombre(req.getNombre())
                .naturaleza(req.getNaturaleza())
                .esSolicitud(Boolean.TRUE.equals(req.getEsSolicitud()))
                .build();
        return ResponseEntity.status(201).body(repo.save(t));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TipoCorrespondencia> actualizar(@PathVariable Long id, @Valid @RequestBody TipoCorrespondenciaRequest req) {
        TipoCorrespondencia t = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tipo no encontrado"));
        t.setNombre(req.getNombre());
        t.setNaturaleza(req.getNaturaleza());
        t.setEsSolicitud(Boolean.TRUE.equals(req.getEsSolicitud()));
        return ResponseEntity.ok(repo.save(t));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestParam boolean activo) {
        TipoCorrespondencia t = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tipo no encontrado"));
        t.setActivo(activo);
        repo.save(t);
        return ResponseEntity.noContent().build();
    }
}