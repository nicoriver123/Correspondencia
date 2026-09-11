package com.tuempresa.correspondencia.scheduler;

import com.tuempresa.correspondencia.entity.Pqrs;
import com.tuempresa.correspondencia.repository.PqrsRepository;
import com.tuempresa.correspondencia.service.EmailService;
import com.tuempresa.correspondencia.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class TerminosPqrsScheduler {
    private final PqrsRepository pqrsRepo;
    private final NotificacionService notifService;
    private final EmailService emailService;

    // Todos los días a las 7:00 AM
    @Scheduled(cron = "0 0 7 * * *")
    public void revisarVencimientos() {
        log.info("Ejecutando job de revisión de términos PQRS...");
        LocalDate hoy = LocalDate.now();
        pqrsRepo.findAll().forEach(p -> {
            String estado = p.getRadicado().getEstado();
            if ("RESPONDIDO".equals(estado) || "CERRADO".equals(estado)) return;
            long dias = ChronoUnit.DAYS.between(hoy, p.getFechaLimiteRespuesta());
            if (dias <= 3 && dias >= 0)
              {
                  if (p.getUsuarioAsignado() != null && p.getUsuarioAsignado().getEmail() != null) {
                      emailService.notificarProximoVencimiento(p.getUsuarioAsignado().getEmail(),
                              p.getRadicado().getNumeroRadicado(), dias);
                  }
                  if (p.getUsuarioAsignado() != null) {
                      notifService.enviar(p.getUsuarioAsignado(),
                              "⚠️ PQRS próxima a vencer",
                              "Radicado " + p.getRadicado().getNumeroRadicado()
                                      + " vence en " + dias + " día(s).",
                              p.getId(), p.getRadicado().getId());
                  }
                  if (p.getRadicado().getDependenciaDestino() != null
                          && p.getRadicado().getDependenciaDestino().getJefe() != null) {
                      notifService.enviar(p.getRadicado().getDependenciaDestino().getJefe(),
                              "🚨 PQRS por vencer en tu dependencia",
                              p.getRadicado().getNumeroRadicado(),
                              p.getId(), p.getRadicado().getId());
                  }
            } else if (dias < 0) {
                log.warn("PQRS VENCIDA: {}", p.getRadicado().getNumeroRadicado());
            }
        });
    }
}