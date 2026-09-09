package com.tuempresa.correspondencia.controller;

import com.tuempresa.correspondencia.entity.Tercero;
import com.tuempresa.correspondencia.repository.TerceroRepository;
import com.tuempresa.correspondencia.service.TerceroService;
import lombok.RequiredArgsConstructor;
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

    // Buscar por identificación antes de crear uno nuevo, para no duplicar.
    @GetMapping("/buscar")
    public ResponseEntity<Tercero> buscar(@RequestParam String identificacion) {
        return repo.findByNumeroIdentificacion(identificacion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public record TerceroRequest(
            String nombreRazonSocial, String numeroIdentificacion, String tipoPersona,
            String email, String telefono, String direccion) {}

    // Si ya existe (misma identificación) lo devuelve; si no, lo crea.
    @PostMapping
    public ResponseEntity<Tercero> obtenerOCrear(@RequestBody TerceroRequest req) {
        Tercero t = terceroService.obtenerOCrear(
                req.nombreRazonSocial(), req.numeroIdentificacion(), req.tipoPersona(),
                req.email(), req.telefono(), req.direccion());
        return ResponseEntity.ok(t);
    }
}
