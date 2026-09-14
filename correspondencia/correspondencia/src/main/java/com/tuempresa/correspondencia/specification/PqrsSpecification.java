package com.tuempresa.correspondencia.specification;

import com.tuempresa.correspondencia.dto.PqrsFilter;
import com.tuempresa.correspondencia.entity.Pqrs;
import com.tuempresa.correspondencia.entity.Radicado;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PqrsSpecification {

    public static Specification<Pqrs> conFiltros(PqrsFilter f) {
        return (root, query, cb) -> {
            Join<Pqrs, Radicado> radicado = root.join("radicado");
            var predicados = cb.conjunction();

            if (f.getTextoLibre() != null && !f.getTextoLibre().isBlank()) {
                String like = "%" + f.getTextoLibre().toLowerCase() + "%";
                predicados = cb.and(predicados, cb.or(
                        cb.like(cb.lower(radicado.get("asunto")), like),
                        cb.like(cb.lower(radicado.get("tercero").get("nombreRazonSocial")), like),
                        cb.like(cb.lower(radicado.get("numeroRadicado")), like)
                ));
            }
            if (f.getTipoCorrespondenciaId() != null) {
                predicados = cb.and(predicados,
                        cb.equal(radicado.get("tipoCorrespondencia").get("id"), f.getTipoCorrespondenciaId()));
            }
            if (f.getCategoriaCorrespondenciaId() != null) {
                predicados = cb.and(predicados,
                        cb.equal(radicado.get("tipoCorrespondencia").get("categoria").get("id"), f.getCategoriaCorrespondenciaId()));
            }
            if (f.getEstado() != null && !f.getEstado().isBlank()) {
                predicados = cb.and(predicados, cb.equal(radicado.get("estado"), f.getEstado()));
            }
            if (f.getDependenciaId() != null) {
                predicados = cb.and(predicados,
                        cb.equal(radicado.get("dependenciaDestino").get("id"), f.getDependenciaId()));
            }
            if (f.getUsuarioAsignadoId() != null) {
                predicados = cb.and(predicados, cb.equal(root.get("usuarioAsignado").get("id"), f.getUsuarioAsignadoId()));
            }
            if (f.getFechaDesde() != null) {
                predicados = cb.and(predicados, cb.greaterThanOrEqualTo(radicado.get("fechaRadicacion").as(LocalDate.class), f.getFechaDesde()));
            }
            if (f.getFechaHasta() != null) {
                predicados = cb.and(predicados, cb.lessThanOrEqualTo(radicado.get("fechaRadicacion").as(LocalDate.class), f.getFechaHasta()));
            }
            if (Boolean.TRUE.equals(f.getSoloVencidos())) {
                predicados = cb.and(predicados,
                        cb.lessThan(root.get("fechaLimiteRespuesta"), LocalDate.now()),
                        cb.not(radicado.get("estado").in("RESPONDIDO", "CERRADO")));
            }
            if (Boolean.TRUE.equals(f.getSoloProximosAVencer())) {
                predicados = cb.and(predicados,
                        cb.between(root.get("fechaLimiteRespuesta"), LocalDate.now(), LocalDate.now().plusDays(3)),
                        cb.not(radicado.get("estado").in("RESPONDIDO", "CERRADO")));
            }
            return predicados;
        };
    }
}