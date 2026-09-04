package com.tuempresa.correspondencia.service;

import com.tuempresa.correspondencia.entity.RefreshToken;
import com.tuempresa.correspondencia.entity.Usuario;
import com.tuempresa.correspondencia.exception.BusinessException;
import com.tuempresa.correspondencia.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository repo;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    /** Resultado de rotar un refresh token: usuario dueño + token nuevo emitido. */
    public record RotacionResultado(Usuario usuario, RefreshToken nuevoToken) {}

    /**
     * Valida y rota el refresh token en UNA sola transacción atómica.
     *
     * findByTokenParaActualizar hace SELECT ... FOR UPDATE: si llegan dos
     * peticiones de refresh casi simultáneas con el mismo token (dos
     * pestañas, doble efecto de React StrictMode, reintento de red), la
     * base de datos serializa: la segunda espera a que la primera termine.
     * Cuando le toca su turno, ya no encuentra el token (fue reemplazado
     * por la primera) y falla de forma controlada con BusinessException,
     * en vez de lanzar una excepción cruda de Hibernate a mitad de commit.
     */
    @Transactional
    public RotacionResultado rotar(String token) {
        RefreshToken actual = repo.findByTokenParaActualizar(token)
                .orElseThrow(() -> new BusinessException("Refresh token inválido"));

        if (Boolean.TRUE.equals(actual.getRevocado())) {
            throw new BusinessException("Refresh token revocado");
        }
        if (actual.getExpiryDate().isBefore(Instant.now())) {
            repo.delete(actual);
            throw new BusinessException("Refresh token expirado");
        }

        Usuario usuario = actual.getUsuario();
        RefreshToken nuevo = crear(usuario);
        return new RotacionResultado(usuario, nuevo);
    }

    /** Usado en login, donde no hay un token previo que validar/rotar. */
    @Transactional
    public RefreshToken crear(Usuario usuario) {
        // DELETE en bloque: no falla aunque otra transacción concurrente
        // ya haya borrado los tokens de este usuario (ver repositorio).
        repo.borrarTodosPorUsuarioId(usuario.getId());

        RefreshToken rt = RefreshToken.builder()
                .usuario(usuario)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshExpirationMs))
                .revocado(false)
                .build();

        return repo.save(rt);
    }

    @Transactional
    public void revocar(String token) {
        repo.findByToken(token).ifPresent(rt -> {
            rt.setRevocado(true);
            repo.save(rt);
        });
    }
}