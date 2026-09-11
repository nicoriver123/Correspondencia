package com.tuempresa.correspondencia.controller;

import com.tuempresa.correspondencia.dto.TerceroFilter;
import com.tuempresa.correspondencia.dto.TerceroRequest;
import com.tuempresa.correspondencia.entity.Tercero;
import com.tuempresa.correspondencia.exception.ResourceNotFoundException;
import com.tuempresa.correspondencia.repository.TerceroRepository;
import com.tuempresa.correspondencia.service.TerceroService;
import com.tuempresa.correspondencia.specification.TerceroSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/terceros")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO','JEFE_DEPENDENCIA')")
public class TerceroController {
    private final TerceroRepository repo;
    private final TerceroService terceroService;

    // Usado por el módulo de Radicados para no duplicar terceros al radicar.
    @GetMapping("/buscar")
    public ResponseEntity<Tercero> buscar(@RequestParam String identificacion) {
        return repo.findByNumeroIdentificacion(identificacion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Tercero> obtenerOCrear(@RequestBody TerceroRequest req) {
        Tercero t = terceroService.obtenerOCrear(
                req.getNombreRazonSocial(), req.getNumeroIdentificacion(), req.getTipoPersona(),
                req.getEmail(), req.getTelefono(), req.getDireccion());
        return ResponseEntity.ok(t);
    }

    // --- Módulo "Gestión de Ciudadanos", exclusivo de ADMIN ---

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Tercero>> listar(TerceroFilter filter, Pageable pageable) {
        return ResponseEntity.ok(repo.findAll(TerceroSpecification.conFiltros(filter), pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tercero> obtener(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Ciudadano no encontrado"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tercero> actualizar(@PathVariable Long id, @Valid @RequestBody TerceroRequest req) {
        Tercero t = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ciudadano no encontrado"));
        t.setNombreRazonSocial(req.getNombreRazonSocial());
        t.setNumeroIdentificacion(req.getNumeroIdentificacion());
        t.setTipoPersona(req.getTipoPersona());
        t.setEmail(req.getEmail());
        t.setTelefono(req.getTelefono());
        t.setDireccion(req.getDireccion());
        return ResponseEntity.ok(repo.save(t));
    }

    // "Eliminar" = desactivar. No se borra de verdad porque queda enlazado a
    // radicados reales; borrarlo destruiría trazabilidad de correspondencia.
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestParam boolean activo) {
        Tercero t = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ciudadano no encontrado"));
        t.setEstado(activo);
        repo.save(t);
        return ResponseEntity.noContent().build();
    }
}
