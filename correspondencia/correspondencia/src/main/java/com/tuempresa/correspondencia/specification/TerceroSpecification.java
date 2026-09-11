package com.tuempresa.correspondencia.specification;

import com.tuempresa.correspondencia.dto.TerceroFilter;
import com.tuempresa.correspondencia.entity.Tercero;
import org.springframework.data.jpa.domain.Specification;

public class TerceroSpecification {

    public static Specification<Tercero> conFiltros(TerceroFilter f) {
        return (root, query, cb) -> {
            var predicados = cb.conjunction();

            if (f.getTextoLibre() != null && !f.getTextoLibre().isBlank()) {
                String like = "%" + f.getTextoLibre().toLowerCase() + "%";
                predicados = cb.and(predicados, cb.or(
                        cb.like(cb.lower(root.get("nombreRazonSocial")), like),
                        cb.like(cb.lower(root.get("numeroIdentificacion")), like),
                        cb.like(cb.lower(root.get("email")), like)
                ));
            }
            if (f.getEstado() != null) {
                predicados = cb.and(predicados, cb.equal(root.get("estado"), f.getEstado()));
            }
            return predicados;
        };
    }
}