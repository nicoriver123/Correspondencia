package com.tuempresa.correspondencia.specification;

import com.tuempresa.correspondencia.dto.RadicadoFilter;
import com.tuempresa.correspondencia.entity.Radicado;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

public class RadicadoSpecification {

    public static Specification<Radicado> conFiltros(RadicadoFilter f) {
        return (root, query, cb) -> {
            Predicate p = cb.conjunction();

            if (f.getTextoLibre() != null && !f.getTextoLibre().isBlank()) {
                String like = "%" + f.getTextoLibre().toLowerCase() + "%";
                Predicate p1 = cb.like(cb.lower(root.get("numeroRadicado")), like);
                Predicate p2 = cb.like(cb.lower(root.get("asunto")), like);
                Predicate p3 = cb.like(cb.lower(root.get("descripcion")), like);
                p = cb.and(p, cb.or(p1, p2, p3));
            }
            if (f.getTipo() != null)
                p = cb.and(p, cb.equal(root.get("tipo"), f.getTipo()));
            if (f.getEstado() != null)
                p = cb.and(p, cb.equal(root.get("estado"), f.getEstado()));
            if (f.getDependenciaId() != null)
                p = cb.and(p, cb.equal(root.get("dependenciaDestino").get("id"), f.getDependenciaId()));
            if (f.getTerceroId() != null)
                p = cb.and(p, cb.equal(root.get("tercero").get("id"), f.getTerceroId()));
            if (f.getFechaDesde() != null) {
                Expression<java.time.LocalDate> fechaExpr = root.get("fechaRadicacion").as(java.time.LocalDate.class);
                p = cb.and(p, cb.greaterThanOrEqualTo(fechaExpr, f.getFechaDesde()));
            }
            if (f.getFechaHasta() != null) {
                Expression<java.time.LocalDate> fechaExpr = root.get("fechaRadicacion").as(java.time.LocalDate.class);
                p = cb.and(p, cb.lessThanOrEqualTo(fechaExpr, f.getFechaHasta()));
            }
            return p;
        };
    }
}
