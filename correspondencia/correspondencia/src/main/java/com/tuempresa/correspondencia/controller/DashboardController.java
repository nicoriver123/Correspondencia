package com.tuempresa.correspondencia.controller;

import com.tuempresa.correspondencia.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','JEFE_DEPENDENCIA','FUNCIONARIO')")
public class DashboardController {

    private final DashboardService service;

    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> resumen(
            @AuthenticationPrincipal UserDetails ud) {

        return ResponseEntity.ok(
                service.getResumen(ud)
        );
    }
}