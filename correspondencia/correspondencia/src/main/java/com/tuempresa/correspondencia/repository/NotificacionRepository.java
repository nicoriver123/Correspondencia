package com.tuempresa.correspondencia.repository;
import com.tuempresa.correspondencia.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioIdAndLeidaFalseOrderByFechaDesc(Long userId);
}