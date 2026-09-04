package com.tuempresa.correspondencia.service;

import com.tuempresa.correspondencia.entity.*;
import com.tuempresa.correspondencia.exception.*;
import com.tuempresa.correspondencia.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnexoService {
    private final AnexoRepository repo;
    private final RadicadoRepository radRepo;

    @Value("${app.uploads.dir}")
    private String uploadsDir;

    public Anexo subir(Long radicadoId, MultipartFile file, Usuario quien) throws IOException {
        Radicado r = radRepo.findById(radicadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Radicado no encontrado"));
        Path dir = Paths.get(uploadsDir);
        if (!Files.exists(dir)) Files.createDirectories(dir);

        String nombreOriginal = file.getOriginalFilename();
        String nombreGuardado = UUID.randomUUID() + "_" + nombreOriginal;
        Path destino = dir.resolve(nombreGuardado);
        Files.copy(file.getInputStream(), destino);

        Anexo a = Anexo.builder()
                .radicado(r)
                .nombreArchivo(nombreOriginal)
                .rutaAlmacenamiento(destino.toString())
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
            Resource r = new UrlResource(Paths.get(a.getRutaAlmacenamiento()).toUri());
            if (!r.exists()) throw new ResourceNotFoundException("Archivo físico no encontrado");
            return r;
        } catch (Exception e) {
            throw new BusinessException("Error al descargar: " + e.getMessage());
        }
    }

    public String getNombreArchivo(Long id) {
        return repo.findById(id).map(Anexo::getNombreArchivo).orElse("archivo");
    }

    public void eliminar(Long id) {
        Anexo a = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Anexo no encontrado"));
        try { Files.deleteIfExists(Paths.get(a.getRutaAlmacenamiento())); } catch (IOException ignored) {}
        repo.delete(a);
    }

    public static class RefreshTokenService {
    }
}