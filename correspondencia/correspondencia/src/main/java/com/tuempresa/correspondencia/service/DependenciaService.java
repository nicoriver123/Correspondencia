package com.tuempresa.correspondencia.service;

import com.tuempresa.correspondencia.dto.DependenciaRequest;
import com.tuempresa.correspondencia.dto.DependenciaResponse;
import com.tuempresa.correspondencia.entity.Dependencia;
import com.tuempresa.correspondencia.entity.Usuario;
import com.tuempresa.correspondencia.exception.BusinessException;
import com.tuempresa.correspondencia.exception.ResourceNotFoundException;
import com.tuempresa.correspondencia.repository.DependenciaRepository;
import com.tuempresa.correspondencia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DependenciaService {
    private final DependenciaRepository repo;
    private final UsuarioRepository usuarioRepo;

    public List<DependenciaResponse> listar(boolean incluirInactivas) {
        List<Dependencia> lista = incluirInactivas ? repo.findAll() : repo.findByActivoTrue();
        return lista.stream().map(this::toDto).toList();
    }

    public DependenciaResponse obtener(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dependencia no encontrada")));
    }

    public DependenciaResponse crear(DependenciaRequest req) {
        if (repo.existsByCodigo(req.getCodigo())) {
            throw new BusinessException("Ya existe una dependencia con ese código.");
        }
        Dependencia d = Dependencia.builder()
                .nombre(req.getNombre())
                .codigo(req.getCodigo())
                .dependenciaPadre(resolverPadre(req.getDependenciaPadreId(), null))
                .jefe(resolverJefe(req.getJefeId()))
                .build();
        return toDto(repo.save(d));
    }

    public DependenciaResponse actualizar(Long id, DependenciaRequest req) {
        Dependencia d = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dependencia no encontrada"));

        if (!d.getCodigo().equals(req.getCodigo()) && repo.existsByCodigo(req.getCodigo())) {
            throw new BusinessException("Ya existe una dependencia con ese código.");
        }

        d.setNombre(req.getNombre());
        d.setCodigo(req.getCodigo());
        d.setDependenciaPadre(resolverPadre(req.getDependenciaPadreId(), id));
        d.setJefe(resolverJefe(req.getJefeId()));
        return toDto(repo.save(d));
    }

    public void cambiarEstado(Long id, boolean activo) {
        Dependencia d = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dependencia no encontrada"));
        d.setActivo(activo);
        repo.save(d);
    }

    private Dependencia resolverPadre(Long padreId, Long idPropio) {
        if (padreId == null) return null;
        if (padreId.equals(idPropio)) {
            throw new BusinessException("Una dependencia no puede ser su propia dependencia padre.");
        }
        return repo.findById(padreId)
                .orElseThrow(() -> new ResourceNotFoundException("Dependencia padre no encontrada"));
    }

    private Usuario resolverJefe(Long jefeId) {
        if (jefeId == null) return null;
        return usuarioRepo.findById(jefeId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (jefe) no encontrado"));
    }

    private DependenciaResponse toDto(Dependencia d) {
        return DependenciaResponse.builder()
                .id(d.getId())
                .nombre(d.getNombre())
                .codigo(d.getCodigo())
                .dependenciaPadreId(d.getDependenciaPadre() != null ? d.getDependenciaPadre().getId() : null)
                .dependenciaPadreNombre(d.getDependenciaPadre() != null ? d.getDependenciaPadre().getNombre() : null)
                .jefeId(d.getJefe() != null ? d.getJefe().getId() : null)
                .jefeNombre(d.getJefe() != null ? d.getJefe().getNombre() : null)
                .activo(d.getActivo())
                .build();
    }
}