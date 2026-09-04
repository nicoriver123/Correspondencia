package com.tuempresa.correspondencia.util;

import com.tuempresa.correspondencia.repository.DiaFestivoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CalculadoraDiasHabiles {
    private final DiaFestivoRepository festivos;

    public LocalDate sumarDiasHabiles(LocalDate desde, int dias) {
        Set<LocalDate> fest = festivos.findByFechaBetween(desde, desde.plusDays(dias * 2))
                .stream().map(f -> f.getFecha()).collect(Collectors.toSet());
        LocalDate actual = desde;
        int cont = 0;
        while (cont < dias) {
            actual = actual.plusDays(1);
            if (actual.getDayOfWeek() != DayOfWeek.SATURDAY &&
                    actual.getDayOfWeek() != DayOfWeek.SUNDAY &&
                    !fest.contains(actual)) {
                cont++;
            }
        }
        return actual;
    }
}