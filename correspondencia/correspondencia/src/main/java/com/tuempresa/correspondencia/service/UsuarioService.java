package com.tuempresa.correspondencia.service;

import com.tuempresa.correspondencia.dto.*;
import com.tuempresa.correspondencia.entity.*;
import com.tuempresa.correspondencia.exception.*;
import com.tuempresa.correspondencia.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepo;
    private final RolRepository rolRepo;
    private final DependenciaRepository depRepo;
    private final PasswordEncoder encoder;

    @Transactional
    public UsuarioResponse crear(UsuarioRequest req) {
        if (usuarioRepo.existsByEmail(req.getEmail()))
            throw new BusinessException("El email ya está registrado");
        Rol rol = rolRepo.findById(req.getRolId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));
        Dependencia dep = req.getDependenciaId() != null
                ? depRepo.findById(req.getDependenciaId()).orElse(null) : null;

        Usuario u = Usuario.builder()
                .nombre(req.getNombre())
                .email(req.getEmail())
                .passwordHash(encoder.encode(req.getPassword()))
                .rol(rol).dependencia(dep).estado(true).build();
        return toDto(usuarioRepo.save(u));
    }

    public Page<UsuarioResponse> listar(Pageable p) {
        return usuarioRepo.findAll(p).map(this::toDto);
    }

    public UsuarioResponse obtener(Long id) {
        return toDto(usuarioRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado")));
    }

    private UsuarioResponse toDto(Usuario u) {
        return UsuarioResponse.builder()
                .id(u.getId()).nombre(u.getNombre()).email(u.getEmail())
                .rol(u.getRol().getNombre())
                .dependencia(u.getDependencia() != null ? u.getDependencia().getNombre() : null)
                .estado(u.getEstado()).build();
    }
}