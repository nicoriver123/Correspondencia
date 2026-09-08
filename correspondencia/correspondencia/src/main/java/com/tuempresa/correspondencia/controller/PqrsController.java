package com.tuempresa.correspondencia.controller;

import com.tuempresa.correspondencia.dto.*;
import com.tuempresa.correspondencia.entity.*;
import com.tuempresa.correspondencia.repository.*;
import com.tuempresa.correspondencia.service.PqrsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/pqrs")
@RequiredArgsConstructor
public class PqrsController {
    private final PqrsService service;
    private final UsuarioRepository userRepo;
    private final PqrsRepository pqrsRepo;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<PqrsResponse> radicar(
            @RequestPart("datos") @Valid PqrsRequest req,
            @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos,
            @AuthenticationPrincipal UserDetails ud) {
        Usuario u = (ud != null)
                ? userRepo.findByEmail(ud.getUsername()).orElseThrow()
                : userRepo.findByEmail("publico@correo.com").orElseThrow();
        return ResponseEntity.status(201).body(service.radicar(req, u, archivos));
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('FUNCIONARIO','ADMIN','JEFE_DEPENDENCIA')")
    public ResponseEntity<Page<PqrsResponse>> listar(
            @org.springframework.web.bind.annotation.ModelAttribute PqrsFilter filter,
            Pageable p,
            @AuthenticationPrincipal UserDetails ud) {

        Usuario u = userRepo.findByEmail(ud.getUsername()).orElseThrow();

        // Cada rol ve solo lo que le corresponde. Se sobreescribe el filtro
        // aquí, en el backend, para que no dependa de lo que mande el cliente
        // (si solo lo filtráramos en el frontend, cualquiera podría llamar
        // el endpoint directamente y ver todo igual).
        switch (u.getRol().getNombre()) {
            case "FUNCIONARIO" -> filter.setUsuarioAsignadoId(u.getId());
            case "JEFE_DEPENDENCIA" -> {
                if (u.getDependencia() != null) {
                    filter.setDependenciaId(u.getDependencia().getId());
                }
            }
            // ADMIN no se restringe: ve todo, con los filtros que el cliente mande.
            default -> {}
        }

        return ResponseEntity.ok(service.listar(filter, p));
    }
    @GetMapping("/{id}")
    public ResponseEntity<PqrsResponse> obtener(@PathVariable Long id,
                                                @AuthenticationPrincipal UserDetails ud) {
        Usuario u = userRepo.findByEmail(ud.getUsername()).orElseThrow();
        return ResponseEntity.ok(service.obtener(id, u));
    }

    @GetMapping("/numero/{numero}/estado")
    public ResponseEntity<PqrsResponse> porNumero(@PathVariable String numero) {
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

    @PostMapping("/{id}/respuesta")
    @PreAuthorize("hasAnyRole('FUNCIONARIO','ADMIN')")
    public ResponseEntity<Void> responder(@PathVariable Long id,
                                          @Valid @RequestBody RespuestaRequest req,
                                          @AuthenticationPrincipal UserDetails ud) {
        Usuario u = userRepo.findByEmail(ud.getUsername()).orElseThrow();
        service.responder(id, req, u);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reabrir")
    @PreAuthorize("hasAnyRole('JEFE_DEPENDENCIA','ADMIN')")
    public ResponseEntity<Void> reabrir(@PathVariable Long id,
                                        @AuthenticationPrincipal UserDetails ud) {
        Usuario u = userRepo.findByEmail(ud.getUsername()).orElseThrow();
        service.reabrir(id, u);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/vencidos")
    @PreAuthorize("hasAnyRole('JEFE_DEPENDENCIA','ADMIN')")
    public ResponseEntity<List<PqrsResponse>> vencidos() {
        return ResponseEntity.ok(service.listarVencidos());
    }

    @GetMapping("/proximas-vencer")
    @PreAuthorize("hasAnyRole('JEFE_DEPENDENCIA','ADMIN','FUNCIONARIO')")
    public ResponseEntity<List<PqrsResponse>> proximasAVencer() {
        return ResponseEntity.ok(service.listarProximosAVencer());
    }
}