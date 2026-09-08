package com.tuempresa.correspondencia.service;

import com.tuempresa.correspondencia.entity.*;
import com.tuempresa.correspondencia.exception.*;
import com.tuempresa.correspondencia.repository.*;
import com.tuempresa.correspondencia.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnexoService {
    private final AnexoRepository repo;
    private final RadicadoRepository radRepo;
    private final StorageService storage;

    public Anexo subir(Long radicadoId, MultipartFile file, Usuario quien) throws IOException {
        Radicado r = radRepo.findById(radicadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Radicado no encontrado"));

        String referencia = storage.guardar(file);

        Anexo a = Anexo.builder()
                .radicado(r)
                .nombreArchivo(file.getOriginalFilename())
                .rutaAlmacenamiento(referencia)
                .tipoMime(file.getContentType())
                .tamanoBytes(file.getSize())
                .usuarioCarga(quien)
                .build();
        return repo.save(a);
    }

    public List<Anexo> listar(Long radicadoId) {
        return repo.findByRadicadoId(radicadoId);
    }

    public Resource descargar(Long id) {
        Anexo a = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Anexo no encontrado"));
        try {
            return storage.cargar(a.getRutaAlmacenamiento());
        } catch (IOException e) {
            throw new BusinessException("Error al descargar: " + e.getMessage());
        }
    }

    public String getNombreArchivo(Long id) {
        return repo.findById(id).map(Anexo::getNombreArchivo).orElse("archivo");
    }

    public void eliminar(Long id) {
        Anexo a = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Anexo no encontrado"));
        storage.eliminar(a.getRutaAlmacenamiento());
        repo.delete(a);
    }
}