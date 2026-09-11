package com.tuempresa.correspondencia.service;

import com.tuempresa.correspondencia.dto.NotificacionResponse;
import com.tuempresa.correspondencia.entity.Notificacion;
import com.tuempresa.correspondencia.entity.Usuario;
import com.tuempresa.correspondencia.exception.ResourceNotFoundException;
import com.tuempresa.correspondencia.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionService {
    private final NotificacionRepository repo;

    public void enviar(Usuario destinatario, String titulo, String mensaje, Long pqrsId, Long radicadoId) {
        repo.save(Notificacion.builder()
                .usuario(destinatario).titulo(titulo).mensaje(mensaje).leida(false)
                .pqrsId(pqrsId).radicadoId(radicadoId).build());
    }

    public List<NotificacionResponse> listarNoLeidas(Long userId) {
        return repo.findByUsuarioIdAndLeidaFalseOrderByFechaDesc(userId).stream()
                .map(this::toDto).toList();
    }

    public void marcarLeida(Long id) {
        Notificacion n = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada"));
        n.setLeida(true);
        repo.save(n);
    }

    private NotificacionResponse toDto(Notificacion n) {
        return NotificacionResponse.builder()
                .id(n.getId()).titulo(n.getTitulo()).mensaje(n.getMensaje())
                .leida(n.getLeida()).fecha(n.getFecha())
                .pqrsId(n.getPqrsId()).radicadoId(n.getRadicadoId()).build();
    }
}