package com.tuempresa.correspondencia.controller;

import com.tuempresa.correspondencia.dto.TipoCorrespondenciaRequest;
import com.tuempresa.correspondencia.entity.CategoriaCorrespondencia;
import com.tuempresa.correspondencia.entity.TipoCorrespondencia;
import com.tuempresa.correspondencia.exception.BusinessException;
import com.tuempresa.correspondencia.exception.ResourceNotFoundException;
import com.tuempresa.correspondencia.repository.CategoriaCorrespondenciaRepository;
import com.tuempresa.correspondencia.repository.TipoCorrespondenciaRepository;
import com.tuempresa.correspondencia.util.Slugificador;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-correspondencia")
@RequiredArgsConstructor
public class TipoCorrespondenciaController {
    private final TipoCorrespondenciaRepository repo;
    private final CategoriaCorrespondenciaRepository categoriaRepo;

    // Igual que arriba: lo necesita el portal público sin sesión.
    @GetMapping
    public ResponseEntity<List<TipoCorrespondencia>> listar(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false, defaultValue = "false") boolean incluirInactivos) {
        List<TipoCorrespondencia> resultado;
        if (categoriaId != null) {
            resultado = incluirInactivos ? repo.findByCategoriaId(categoriaId) : repo.findByCategoriaIdAndActivoTrue(categoriaId);
        } else {
            resultado = incluirInactivos ? repo.findAll() : repo.findByActivoTrue();
        }
        return ResponseEntity.ok(resultado);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TipoCorrespondencia> crear(@Valid @RequestBody TipoCorrespondenciaRequest req) {
        CategoriaCorrespondencia categoria = categoriaRepo.findById(req.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        TipoCorrespondencia t = TipoCorrespondencia.builder()
                .categoria(categoria)
                .codigo(generarCodigoUnico(req.getNombre()))
                .nombre(req.getNombre())
                .descripcion(req.getDescripcion())
                .diasTermino(req.getDiasTermino())
                .build();
        return ResponseEntity.status(201).body(repo.save(t));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TipoCorrespondencia> actualizar(@PathVariable Long id, @Valid @RequestBody TipoCorrespondenciaRequest req) {
        TipoCorrespondencia t = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tipo no encontrado"));
        CategoriaCorrespondencia categoria = categoriaRepo.findById(req.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        t.setCategoria(categoria);
        t.setNombre(req.getNombre());
        t.setDescripcion(req.getDescripcion());
        t.setDiasTermino(req.getDiasTermino());
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