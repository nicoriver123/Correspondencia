package com.tuempresa.correspondencia.service;

import com.tuempresa.correspondencia.dto.ReporteConteo;
import com.tuempresa.correspondencia.entity.Pqrs;
import com.tuempresa.correspondencia.repository.PqrsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {
    private final PqrsRepository pqrsRepo;

    public List<ReporteConteo> porTipo() {
        Map<String, Long> map = pqrsRepo.findAll().stream()
                .collect(Collectors.groupingBy(Pqrs::getTipoPqrs, Collectors.counting()));
        return map.entrySet().stream()
                .map(e -> new ReporteConteo(e.getKey(), e.getValue())).toList();
    }

    public Map<String, Object> cumplimientoTerminos() {
        List<Pqrs> todos = pqrsRepo.findAll();
        long total = todos.size();
        long vencidos = todos.stream().filter(p -> {
            String e = p.getRadicado().getEstado();
            return !("RESPONDIDO".equals(e) || "CERRADO".equals(e))
                    && LocalDate.now().isAfter(p.getFechaLimiteRespuesta());
        }).count();
        long aTiempo = total - vencidos;
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("total", total);
        r.put("aTiempo", aTiempo);
        r.put("vencidos", vencidos);
        r.put("porcentajeCumplimiento", total == 0 ? 0 : (aTiempo * 100.0 / total));
        return r;
    }
}