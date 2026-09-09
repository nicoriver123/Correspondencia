package com.tuempresa.correspondencia.controller;

import com.tuempresa.correspondencia.entity.TipoDocumento;
import com.tuempresa.correspondencia.repository.TipoDocumentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tipos-documento")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO','JEFE_DEPENDENCIA')")
public class TipoDocumentoController {
    private final TipoDocumentoRepository repo;

    @GetMapping
    public ResponseEntity<List<TipoDocumento>> listar() {
        return ResponseEntity.ok(repo.findAll());
    }
}