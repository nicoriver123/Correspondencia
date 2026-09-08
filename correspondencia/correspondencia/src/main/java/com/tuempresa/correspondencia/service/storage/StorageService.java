package com.tuempresa.correspondencia.service.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface StorageService {
    /** Guarda el archivo y devuelve una referencia (hoy: ruta en disco; mañana: key de S3). */
    String guardar(MultipartFile file) throws IOException;
    Resource cargar(String referencia) throws IOException;
    void eliminar(String referencia);
}