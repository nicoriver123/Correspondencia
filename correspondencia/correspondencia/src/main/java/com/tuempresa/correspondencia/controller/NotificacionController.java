package com.tuempresa.correspondencia.controller;

import com.tuempresa.correspondencia.dto.NotificacionResponse;
import com.tuempresa.correspondencia.entity.Usuario;
import com.tuempresa.correspondencia.repository.UsuarioRepository;
import com.tuempresa.correspondencia.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {
    private final NotificacionService service;
    private final UsuarioRepository userRepo;

    @GetMapping
    public ResponseEntity<List<NotificacionResponse>> listar(@AuthenticationPrincipal UserDetails ud) {
        Usuario u = userRepo.findByEmail(ud.getUsername()).orElseThrow();
        return ResponseEntity.ok(service.listarNoLeidas(u.getId()));
    }

    @PatchMapping("/{id}/leer")
    public ResponseEntity<Void> marcarLeida(@PathVariable Long id) {
        service.marcarLeida(id);
        return ResponseEntity.noContent().build();
    }
}