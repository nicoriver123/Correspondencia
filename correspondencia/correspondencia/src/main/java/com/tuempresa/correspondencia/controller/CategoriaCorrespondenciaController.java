package com.tuempresa.correspondencia.controller;

import com.tuempresa.correspondencia.dto.CategoriaCorrespondenciaRequest;
import com.tuempresa.correspondencia.entity.CategoriaCorrespondencia;
import com.tuempresa.correspondencia.exception.BusinessException;
import com.tuempresa.correspondencia.exception.ResourceNotFoundException;
import com.tuempresa.correspondencia.repository.CategoriaCorrespondenciaRepository;
import com.tuempresa.correspondencia.util.Slugificador;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias-correspondencia")
@RequiredArgsConstructor
public class CategoriaCorrespondenciaController {
    private final CategoriaCorrespondenciaRepository repo;

    // Sin @PreAuthorize aquí a propósito: lo necesita el portal público
    // (ciudadano sin sesión) para el formulario de radicar. Es de solo
    // lectura y no expone nada sensible.
    @GetMapping
    public ResponseEntity<List<CategoriaCorrespondencia>> listar(
            @RequestParam(required = false, defaultValue = "false") boolean incluirInactivos) {
        return ResponseEntity.ok(incluirInactivos ? repo.findAll() : repo.findByActivoTrue());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaCorrespondencia> crear(@Valid @RequestBody CategoriaCorrespondenciaRequest req) {
        String codigo = generarCodigoUnico(req.getNombre());
        CategoriaCorrespondencia c = CategoriaCorrespondencia.builder()
                .codigo(codigo).nombre(req.getNombre()).descripcion(req.getDescripcion())
                .build();
        return ResponseEntity.status(201).body(repo.save(c));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaCorrespondencia> actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaCorrespondenciaRequest req) {
        CategoriaCorrespondencia c = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        c.setNombre(req.getNombre());
        c.setDescripcion(req.getDescripcion());
        return ResponseEntity.ok(repo.save(c));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestParam boolean activo) {
        CategoriaCorrespondencia c = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        c.setActivo(activo);
        repo.save(c);
        return ResponseEntity.noContent().build();
    }

    private String generarCodigoUnico(String nombre) {
        String base = Slugificador.generar(nombre);
        if (base.isBlank()) throw new BusinessException("El nombre no genera un código válido.");
        String candidato = base;
        int i = 1;
        while (repo.existsByCodigo(candidato)) {
            candidato = base + "_" + (++i);
        }
        return candidato;
    }
}