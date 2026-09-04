package com.tuempresa.correspondencia.specification;

import com.tuempresa.correspondencia.dto.PqrsFilter;
import com.tuempresa.correspondencia.entity.Pqrs;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;
import java.time.LocalDate;

public class PqrsSpecification {

    public static Specification<Pqrs> conFiltros(PqrsFilter f) {
        return (root, query, cb) -> {
            Predicate p = cb.conjunction();
            Join<Object, Object> rad = root.join("radicado", JoinType.LEFT);

            if (f.getTextoLibre() != null && !f.getTextoLibre().isBlank()) {
                String like = "%" + f.getTextoLibre().toLowerCase() + "%";
                Predicate p1 = cb.like(cb.lower(rad.get("numeroRadicado")), like);
                Predicate p2 = cb.like(cb.lower(rad.get("asunto")), like);
                Predicate p3 = cb.like(cb.lower(rad.get("descripcion")), like);
                p = cb.and(p, cb.or(p1, p2, p3));
            }
            if (f.getTipoPqrs() != null)
                p = cb.and(p, cb.equal(root.get("tipoPqrs"), f.getTipoPqrs()));
            if (f.getCategoriaId() != null)
                p = cb.and(p, cb.equal(root.get("categoria").get("id"), f.getCategoriaId()));
            if (f.getEstado() != null)
                p = cb.and(p, cb.equal(rad.get("estado"), f.getEstado()));
            if (f.getDependenciaId() != null)
                p = cb.and(p, cb.equal(rad.get("dependenciaDestino").get("id"), f.getDependenciaId()));
            if (f.getUsuarioAsignadoId() != null)
                p = cb.and(p, cb.equal(root.get("usuarioAsignado").get("id"), f.getUsuarioAsignadoId()));
            if (f.getFechaDesde() != null) {
                Expression<LocalDate> fechaExpr = rad.get("fechaRadicacion").as(LocalDate.class);
                p = cb.and(p, cb.greaterThanOrEqualTo(fechaExpr, f.getFechaDesde()));
            }
            if (f.getFechaHasta() != null) {
                Expression<LocalDate> fechaExpr = rad.get("fechaRadicacion").as(LocalDate.class);
                p = cb.and(p, cb.lessThanOrEqualTo(fechaExpr, f.getFechaHasta()));
            }
            if (Boolean.TRUE.equals(f.getSoloVencidos())) {
                p = cb.and(p, cb.lessThan(root.get("fechaLimiteRespuesta"), LocalDate.now()));
                p = cb.and(p, cb.notEqual(rad.get("estado"), "RESPONDIDO"));
                p = cb.and(p, cb.notEqual(rad.get("estado"), "CERRADO"));
            }
            if (Boolean.TRUE.equals(f.getSoloProximosAVencer())) {
                LocalDate hoy = LocalDate.now();
                LocalDate en3 = hoy.plusDays(3);
                p = cb.and(p, cb.between(root.get("fechaLimiteRespuesta"), hoy, en3));
                p = cb.and(p, cb.notEqual(rad.get("estado"), "RESPONDIDO"));
                p = cb.and(p, cb.notEqual(rad.get("estado"), "CERRADO"));
            }
            return p;
        };
    }
}