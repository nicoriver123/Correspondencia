package com.tuempresa.correspondencia.service;

import com.tuempresa.correspondencia.dto.DependenciaRequest;
import com.tuempresa.correspondencia.entity.Dependencia;
import com.tuempresa.correspondencia.entity.Usuario;
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
    private final UsuarioRepository userRepo;

    public Dependencia crear(DependenciaRequest req) {
        Dependencia padre = req.getDependenciaPadreId() != null
                ? repo.findById(req.getDependenciaPadreId()).orElse(null) : null;
        Usuario jefe = req.getJefeId() != null
                ? userRepo.findById(req.getJefeId()).orElse(null) : null;
        return repo.save(Dependencia.builder()
                .nombre(req.getNombre()).codigo(req.getCodigo())
                .dependenciaPadre(padre).jefe(jefe).build());
    }

    public Dependencia actualizar(Long id, DependenciaRequest req) {
        Dependencia d = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dependencia no encontrada"));
        d.setNombre(req.getNombre());
        d.setCodigo(req.getCodigo());
        if (req.getDependenciaPadreId() != null)
            d.setDependenciaPadre(repo.findById(req.getDependenciaPadreId()).orElse(null));
        if (req.getJefeId() != null)
            d.setJefe(userRepo.findById(req.getJefeId()).orElse(null));
        return repo.save(d);
    }

    public List<Dependencia> listar() { return repo.findAll(); }
    public List<Dependencia> listarRaices() { return repo.findByDependenciaPadreIsNull(); }
}