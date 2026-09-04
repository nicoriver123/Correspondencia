package com.tuempresa.correspondencia.controller;

import com.tuempresa.correspondencia.entity.CategoriaPqrs;
import com.tuempresa.correspondencia.repository.CategoriaPqrsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categorias-pqrs")
@RequiredArgsConstructor
public class CategoriaPqrsController {
    private final CategoriaPqrsRepository repo;

    @GetMapping
    public ResponseEntity<List<CategoriaPqrs>> listar() {
        return ResponseEntity.ok(repo.findAll());
    }
}