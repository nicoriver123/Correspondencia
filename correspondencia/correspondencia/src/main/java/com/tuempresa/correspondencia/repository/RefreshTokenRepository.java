package com.tuempresa.correspondencia.repository;

import com.tuempresa.correspondencia.entity.RefreshToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    /**
     * SELECT ... FOR UPDATE: bloquea la fila a nivel de base de datos.
     * Si dos peticiones de refresh llegan casi al mismo tiempo con el mismo
     * token (dos pestañas, doble efecto de React StrictMode, reintento de
     * red, etc.), la segunda espera a que la primera termine su transacción
     * en vez de correr en paralelo y chocar.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.token = :token")
    Optional<RefreshToken> findByTokenParaActualizar(String token);

    // Usamos findAll para que Hibernate maneje el ciclo de vida de la entidad correctamente
    List<RefreshToken> findAllByUsuarioId(Long usuarioId);

    /**
     * DELETE en bloque (una sola sentencia SQL), a diferencia de borrar
     * entidad por entidad con repo.delete(). Un delete por entidad espera
     * borrar exactamente 1 fila y lanza StaleObjectStateException si otra
     * transacción ya la borró; un DELETE en bloque simplemente borra las
     * filas que existan (0, 1 o más) sin esa verificación, así que nunca
     * falla por una condición de carrera.
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM RefreshToken rt WHERE rt.usuario.id = :usuarioId")
    void borrarTodosPorUsuarioId(Long usuarioId);
}