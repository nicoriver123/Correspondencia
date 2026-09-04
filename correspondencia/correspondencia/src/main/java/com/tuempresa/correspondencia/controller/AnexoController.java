package com.tuempresa.correspondencia.controller;

import com.tuempresa.correspondencia.entity.*;
import com.tuempresa.correspondencia.repository.UsuarioRepository;
import com.tuempresa.correspondencia.service.AnexoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnexoController {
    private final AnexoService service;
    private final UsuarioRepository userRepo;

    @PostMapping("/radicados/{id}/anexos")
    public ResponseEntity<Anexo> subir(@PathVariable Long id,
                                       @RequestParam("file") MultipartFile file,
                                       @AuthenticationPrincipal UserDetails ud) throws IOException {
        Usuario u = userRepo.findByEmail(ud.getUsername()).orElseThrow();
        return ResponseEntity.status(201).body(service.subir(id, file, u));
    }

    @GetMapping("/radicados/{id}/anexos")
    public ResponseEntity<List<Anexo>> listar(@PathVariable Long id) {
        return ResponseEntity.ok(service.listar(id));
    }

    @GetMapping("/anexos/{id}/descargar")
    public ResponseEntity<Resource> descargar(@PathVariable Long id) {
        Resource r = service.descargar(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + service.getNombreArchivo(id) + "\"")
                .body(r);
    }

    @DeleteMapping("/anexos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}