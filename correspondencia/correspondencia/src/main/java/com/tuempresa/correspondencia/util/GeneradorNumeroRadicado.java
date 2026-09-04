package com.tuempresa.correspondencia.util;

import com.tuempresa.correspondencia.repository.RadicadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
@RequiredArgsConstructor
public class GeneradorNumeroRadicado {
    private final RadicadoRepository repo;

    public synchronized String generar(String tipo) {
        String year = String.valueOf(Year.now().getValue());
        String prefijo = year + "-" + tipo + "-";
        String ultimo = repo.findUltimoNumeroPorPrefijo(prefijo).orElse(null);
        int consecutivo = 1;
        if (ultimo != null) {
            String[] partes = ultimo.split("-");
            consecutivo = Integer.parseInt(partes[partes.length - 1]) + 1;
        }
        return prefijo + String.format("%06d", consecutivo);
    }
}