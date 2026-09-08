package com.tuempresa.correspondencia.service;

import com.tuempresa.correspondencia.entity.Pqrs;
import com.tuempresa.correspondencia.entity.Usuario;
import com.tuempresa.correspondencia.repository.PqrsRepository;
import com.tuempresa.correspondencia.repository.RadicadoRepository;
import com.tuempresa.correspondencia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PqrsRepository pqrsRepo;
    private final RadicadoRepository radRepo;
    private final UsuarioRepository userRepo;


    public Map<String, Object> getResumen(UserDetails ud) {

        // ============================================================
        // USUARIO AUTENTICADO
        // ============================================================

        Usuario usuario = userRepo
                .findByEmail(ud.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("Usuario autenticado no encontrado")
                );

        String rol = usuario.getRol().getNombre();


        // ============================================================
        // OBTENER LAS PQRS QUE PUEDE VER EL USUARIO
        // ============================================================

        List<Pqrs> todas = pqrsRepo.findAll();

        if ("FUNCIONARIO".equals(rol)) {

            // El funcionario solamente ve las PQRS
            // que están asignadas a él.

            todas = todas.stream()
                    .filter(p ->
                            p.getUsuarioAsignado() != null
                                    && p.getUsuarioAsignado()
                                    .getId()
                                    .equals(usuario.getId())
                    )
                    .toList();

        } else if ("JEFE_DEPENDENCIA".equals(rol)) {

            // El jefe solamente ve las PQRS
            // pertenecientes a su dependencia.

            Long dependenciaId = usuario.getDependencia() != null
                    ? usuario.getDependencia().getId()
                    : null;

            if (dependenciaId == null) {

                todas = List.of();

            } else {

                todas = todas.stream()
                        .filter(p ->
                                p.getRadicado() != null
                                        && p.getRadicado()
                                        .getDependenciaDestino() != null
                                        && p.getRadicado()
                                        .getDependenciaDestino()
                                        .getId()
                                        .equals(dependenciaId)
                        )
                        .toList();
            }

        }

        // ADMIN:
        // No se aplica ningún filtro.
        // Por lo tanto ve todas las PQRS.


        // ============================================================
        // FECHA ACTUAL
        // ============================================================

        LocalDate hoy = LocalDate.now();


        // ============================================================
        // KPIs PRINCIPALES
        // ============================================================

        long total = todas.size();


        long enTramite = todas.stream()
                .filter(p ->
                        p.getRadicado() != null
                                && "EN_TRAMITE".equals(
                                p.getRadicado().getEstado()
                        )
                )
                .count();


        long respondidas = todas.stream()
                .filter(p ->
                        p.getRadicado() != null
                                && "RESPONDIDO".equals(
                                p.getRadicado().getEstado()
                        )
                )
                .count();


        long cerradas = todas.stream()
                .filter(p ->
                        p.getRadicado() != null
                                && "CERRADO".equals(
                                p.getRadicado().getEstado()
                        )
                )
                .count();


        // ============================================================
        // VENCIDAS
        // ============================================================

        long vencidos = todas.stream()
                .filter(p -> {

                    if (p.getRadicado() == null) {
                        return false;
                    }

                    String estado =
                            p.getRadicado().getEstado();

                    if ("RESPONDIDO".equals(estado)
                            || "CERRADO".equals(estado)) {
                        return false;
                    }

                    if (p.getFechaLimiteRespuesta() == null) {
                        return false;
                    }

                    return hoy.isAfter(
                            p.getFechaLimiteRespuesta()
                    );
                })
                .count();


        // ============================================================
        // PRÓXIMAS A VENCER
        // ============================================================

        long proximosAVencer = todas.stream()
                .filter(p -> {

                    if (p.getRadicado() == null) {
                        return false;
                    }

                    String estado =
                            p.getRadicado().getEstado();

                    if ("RESPONDIDO".equals(estado)
                            || "CERRADO".equals(estado)) {
                        return false;
                    }

                    if (p.getFechaLimiteRespuesta() == null) {
                        return false;
                    }

                    long dias = ChronoUnit.DAYS.between(
                            hoy,
                            p.getFechaLimiteRespuesta()
                    );

                    return dias >= 0 && dias <= 3;
                })
                .count();


        // ============================================================
        // PQRS A TIEMPO
        // ============================================================

        long aTiempo = Math.max(
                0,
                enTramite - vencidos - proximosAVencer
        );


        // ============================================================
        // CUMPLIMIENTO
        // ============================================================

        double cumplimiento;

        if (total == 0) {

            cumplimiento = 100;

        } else {

            cumplimiento =
                    (respondidas + cerradas)
                            * 100.0
                            / total;
        }


        // ============================================================
        // PQRS POR TIPO
        // ============================================================

        Map<String, Long> porTipo = todas.stream()
                .filter(p -> p.getTipoPqrs() != null)
                .collect(
                        Collectors.groupingBy(
                                Pqrs::getTipoPqrs,
                                Collectors.counting()
                        )
                );


        // ============================================================
        // RADICADOS HOY
        // ============================================================

        long radicadosHoy;

        if ("FUNCIONARIO".equals(rol)) {

            // Un funcionario solamente debe ver
            // sus PQRS asignadas que fueron radicadas hoy.

            radicadosHoy = todas.stream()
                    .filter(p ->
                            p.getRadicado() != null
                                    && p.getRadicado()
                                    .getFechaRadicacion() != null
                                    && p.getRadicado()
                                    .getFechaRadicacion()
                                    .toLocalDate()
                                    .equals(hoy)
                    )
                    .count();

        } else if ("JEFE_DEPENDENCIA".equals(rol)) {

            // Como "todas" ya está filtrado por dependencia,
            // solamente contamos esos radicados.

            radicadosHoy = todas.stream()
                    .filter(p ->
                            p.getRadicado() != null
                                    && p.getRadicado()
                                    .getFechaRadicacion() != null
                                    && p.getRadicado()
                                    .getFechaRadicacion()
                                    .toLocalDate()
                                    .equals(hoy)
                    )
                    .count();

        } else {

            // ADMIN ve todos los radicados de hoy.

            radicadosHoy = radRepo.findAll()
                    .stream()
                    .filter(r ->
                            r.getFechaRadicacion() != null
                                    && r.getFechaRadicacion()
                                    .toLocalDate()
                                    .equals(hoy)
                    )
                    .count();
        }


        // ============================================================
        // RESPUESTA
        // ============================================================

        Map<String, Object> resumen =
                new LinkedHashMap<>();

        resumen.put("totalPqrs", total);
        resumen.put("enTramite", enTramite);
        resumen.put("respondidas", respondidas);
        resumen.put("cerradas", cerradas);
        resumen.put("vencidos", vencidos);
        resumen.put("proximosAVencer", proximosAVencer);
        resumen.put("aTiempo", aTiempo);

        resumen.put(
                "cumplimiento",
                Math.round(cumplimiento * 100.0) / 100.0
        );

        resumen.put("radicadosHoy", radicadosHoy);
        resumen.put("porTipo", porTipo);

        // Información útil para el frontend
        resumen.put("rol", rol);

        return resumen;
    }
}