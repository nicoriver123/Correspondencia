package com.tuempresa.correspondencia.service;

import com.tuempresa.correspondencia.entity.Tercero;
import com.tuempresa.correspondencia.repository.TerceroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TerceroService {
    private final TerceroRepository repo;

    public Tercero obtenerOCrear(String nombre, String identificacion, String tipoPersona,
                                 String email, String telefono, String direccion) {
        return repo.findByNumeroIdentificacion(identificacion)
                .map(t -> {
                    t.setNombreRazonSocial(nombre);
                    t.setEmail(email);
                    t.setTelefono(telefono);
                    t.setDireccion(direccion);
                    return repo.save(t);
                })
                .orElseGet(() -> repo.save(Tercero.builder()
                        .nombreRazonSocial(nombre)
                        .numeroIdentificacion(identificacion)
                        .tipoPersona(tipoPersona)
                        .email(email).telefono(telefono).direccion(direccion)
                        .build()));
    }
}