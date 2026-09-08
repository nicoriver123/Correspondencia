package com.tuempresa.correspondencia.service.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Slf4j
@Service
public class LocalFileStorageService implements StorageService {

    // IMPORTANTE: en producción, esta ruta debe:
    //  1) ser ABSOLUTA (ej. /var/data/correspondencia/uploads en Linux),
    //  2) estar FUERA de la carpeta donde se despliega el .jar,
    //  3) vivir en un disco/volumen que NO se borre al redeployar.
    // Se configura por variable de entorno, nunca hardcodeada en el código,
    // así cada ambiente (tu PC, el servidor) usa la suya sin tocar nada.
    @Value("${app.uploads.dir}")
    private String uploadsDir;

    @Override
    public String guardar(MultipartFile file) throws IOException {
        Path dir = Paths.get(uploadsDir);
        if (!Files.exists(dir)) Files.createDirectories(dir);

        String nombreGuardado = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path destino = dir.resolve(nombreGuardado);
        Files.copy(file.getInputStream(), destino);
        return destino.toString();
    }

    @Override
    public Resource cargar(String referencia) throws IOException {
        Resource r = new UrlResource(Paths.get(referencia).toUri());
        if (!r.exists()) throw new IOException("Archivo físico no encontrado: " + referencia);
        return r;
    }

    @Override
    public void eliminar(String referencia) {
        try {
            Files.deleteIfExists(Paths.get(referencia));
        } catch (IOException e) {
            log.warn("No se pudo borrar el archivo físico {}: {}", referencia, e.getMessage());
        }
    }
}