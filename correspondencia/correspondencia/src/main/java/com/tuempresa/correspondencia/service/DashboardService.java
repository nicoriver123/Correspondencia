package com.tuempresa.correspondencia.service;

import com.tuempresa.correspondencia.entity.Pqrs;
import com.tuempresa.correspondencia.repository.PqrsRepository;
import com.tuempresa.correspondencia.repository.RadicadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final PqrsRepository pqrsRepo;
    private final RadicadoRepository radRepo;

    public Map<String, Object> getResumen() {
        List<Pqrs> todas = pqrsRepo.findAll();
        LocalDate hoy = LocalDate.now();

        // KPIs principales
        long total = todas.size();
        long enTramite = todas.stream()
                .filter(p -> "EN_TRAMITE".equals(p.getRadicado().getEstado()))
                .count();
        long respondidas = todas.stream()
                .filter(p -> "RESPONDIDO".equals(p.getRadicado().getEstado()))
                .count();
        long cerradas = todas.stream()
                .filter(p -> "CERRADO".equals(p.getRadicado().getEstado()))
                .count();

        // Vencidos: hoy > fecha_limite Y estado != RESPONDIDO/CERRADO
        long vencidos = todas.stream()
                .filter(p -> {
                    String e = p.getRadicado().getEstado();
                    return !("RESPONDIDO".equals(e) || "CERRADO".equals(e))
                            && p.getFechaLimiteRespuesta() != null
                            && hoy.isAfter(p.getFechaLimiteRespuesta());
                })
                .count();

        // Próximos a vencer: ≤ 3 días hábiles restantes
        long proximosAVencer = todas.stream()
                .filter(p -> {
                    String e = p.getRadicado().getEstado();
                    if ("RESPONDIDO".equals(e) || "CERRADO".equals(e)) return false;
                    if (p.getFechaLimiteRespuesta() == null) return false;
                    long dias = java.time.temporal.ChronoUnit.DAYS.between(hoy, p.getFechaLimiteRespuesta());
                    return dias >= 0 && dias <= 3;
                })
                .count();

        // A tiempo (en trámite pero aún no vence)
        long aTiempo = enTramite - vencidos - proximosAVencer;

        // Cumplimiento
        double cumplimiento = total == 0 ? 100 : ((respondidas + cerradas) * 100.0 / total);

        // PQRS por tipo (para gráfico de torta)
        Map<String, Long> porTipo = todas.stream()
                .collect(Collectors.groupingBy(Pqrs::getTipoPqrs, Collectors.counting()));

        // Radicados hoy
        long radicadosHoy = radRepo.findAll().stream()
                .filter(r -> r.getFechaRadicacion() != null
                        && r.getFechaRadicacion().toLocalDate().equals(hoy))
                .count();

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("totalPqrs", total);
        resumen.put("enTramite", enTramite);
        resumen.put("respondidas", respondidas);
        resumen.put("cerradas", cerradas);
        resumen.put("vencidos", vencidos);
        resumen.put("proximosAVencer", proximosAVencer);
        resumen.put("aTiempo", aTiempo);
        resumen.put("cumplimiento", Math.round(cumplimiento * 100.0) / 100.0);
        resumen.put("radicadosHoy", radicadosHoy);
        resumen.put("porTipo", porTipo);
        return resumen;
    }
}