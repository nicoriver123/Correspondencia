package com.tuempresa.correspondencia.controller;

import com.tuempresa.correspondencia.dto.*;
import com.tuempresa.correspondencia.exception.BusinessException;
import com.tuempresa.correspondencia.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest req,
            HttpServletResponse response) {

        LoginResponse loginData = authService.login(req);

        // Crear cookie HTTP-Only para el Refresh Token
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", loginData.getRefreshToken())
                .httpOnly(true)
                .secure(false)  // ⚠️ FALSE para desarrollo en localhost (HTTP)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", refreshCookie.toString());

        return ResponseEntity.ok(new LoginResponse(
                loginData.getAccessToken(),
                null,
                loginData.getTipo(),
                loginData.getUsuarioId(),
                loginData.getNombre(),
                loginData.getRol()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            log.debug("Refresh rechazado: no llegó cookie de refreshToken");
            return ResponseEntity.status(401).build();
        }

        try {
            RefreshResponse refreshData = authService.refresh(refreshToken);

            ResponseCookie newCookie = ResponseCookie.from("refreshToken", refreshData.getRefreshToken())
                    .httpOnly(true)
                    .secure(false) // FALSE para localhost HTTP
                    .path("/")
                    .maxAge(Duration.ofDays(7))
                    .sameSite("Lax")
                    .build();

            response.addHeader("Set-Cookie", newCookie.toString());
            return ResponseEntity.ok(refreshData);

        } catch (BusinessException e) {
            // Token inválido/expirado/revocado, o ya rotado por una petición
            // concurrente: es un caso esperado, no un error del sistema.
            log.debug("Refresh rechazado: {}", e.getMessage());
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            log.error("Error inesperado al refrescar token", e);
            return ResponseEntity.status(401).build();
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken != null && !refreshToken.isEmpty()) {
            authService.logout(refreshToken);
        }

        // 4. Eliminar la cookie del navegador
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(frontendUrl.startsWith("https://"))
                .path("/")
                .maxAge(Duration.ZERO) // Expira inmediatamente
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", deleteCookie.toString());

        return ResponseEntity.noContent().build();
    }

    // Endpoint opcional para que el frontend sepa quién es al recargar
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(Map.of("email", userDetails.getUsername()));
    }
}