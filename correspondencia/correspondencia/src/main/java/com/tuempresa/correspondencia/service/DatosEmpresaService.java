package com.tuempresa.correspondencia.service;

import com.tuempresa.correspondencia.dto.DatosEmpresaRequest;
import com.tuempresa.correspondencia.entity.DatosEmpresa;
import com.tuempresa.correspondencia.exception.BusinessException;
import com.tuempresa.correspondencia.exception.ResourceNotFoundException;
import com.tuempresa.correspondencia.repository.DatosEmpresaRepository;
import com.tuempresa.correspondencia.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DatosEmpresaService {
    private final DatosEmpresaRepository repo;
    private final StorageService storage;

    private static final Long ID_UNICO = 1L;
    private static final long LOGO_MAX_BYTES = 2 * 1024 * 1024; // 2MB
    private static final Set<String> LOGO_TIPOS_PERMITIDOS = Set.of(
            "image/png", "image/jpeg", "image/svg+xml", "image/webp");

    public DatosEmpresa obtener() {
        return repo.findById(ID_UNICO)
                .orElseGet(() -> repo.save(DatosEmpresa.builder().id(ID_UNICO).build()));
    }

    @Transactional
    public DatosEmpresa actualizar(DatosEmpresaRequest req, MultipartFile logo) throws IOException {
        DatosEmpresa d = obtener();
        d.setNit(req.getNit());
        d.setRazonSocial(req.getRazonSocial());
        d.setNombreComercial(req.getNombreComercial());
        d.setRepresentanteLegal(req.getRepresentanteLegal());
        d.setDireccion(req.getDireccion());
        d.setTelefono(req.getTelefono());
        d.setEmail(req.getEmail());
        d.setSitioWeb(req.getSitioWeb());

        if (logo != null && !logo.isEmpty()) {
            validarLogo(logo);
            if (d.getLogoRuta() != null) {
                storage.eliminar(d.getLogoRuta()); // borra el anterior, no lo dejamos huérfano
            }
            d.setLogoRuta(storage.guardar(logo));
            d.setLogoTipoMime(logo.getContentType());
        }

        return repo.save(d);
    }

    public Resource cargarLogo() throws IOException {
        DatosEmpresa d = obtener();
        if (d.getLogoRuta() == null) {
            throw new ResourceNotFoundException("Todavía no se ha cargado un logo.");
        }
        return storage.cargar(d.getLogoRuta());
    }

    private void validarLogo(MultipartFile file) {
        if (file.getSize() > LOGO_MAX_BYTES) {
            throw new BusinessException("El logo no puede superar 2MB.");
        }
        if (!LOGO_TIPOS_PERMITIDOS.contains(file.getContentType())) {
            throw new BusinessException("El logo debe ser PNG, JPG, SVG o WEBP.");
        }
    }
}