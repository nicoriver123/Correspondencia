package com.tuempresa.correspondencia.service;

import com.tuempresa.correspondencia.dto.*;
import com.tuempresa.correspondencia.entity.*;
import com.tuempresa.correspondencia.exception.*;
import com.tuempresa.correspondencia.repository.*;
import com.tuempresa.correspondencia.specification.RadicadoSpecification;
import com.tuempresa.correspondencia.util.GeneradorNumeroRadicado;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RadicadoService {
    private final RadicadoRepository repo;
    private final TerceroRepository terceroRepo;
    private final DependenciaRepository depRepo;
    private final UsuarioRepository userRepo;
    private final TipoDocumentoRepository tipoDocRepo;
    private final HistorialTrazabilidadRepository histRepo;
    private final GeneradorNumeroRadicado generador;
    private final NotificacionService notifService;
    private final RespuestaRepository respRepo;

    @Transactional
    public RadicadoResponse crear(RadicadoRequest req, Usuario quienRadica) {
        Tercero tercero = terceroRepo.findById(req.getTerceroId())
                .orElseThrow(() -> new ResourceNotFoundException("Tercero no encontrado"));
        Dependencia destino = depRepo.findById(req.getDependenciaDestinoId())
                .orElseThrow(() -> new ResourceNotFoundException("Dependencia destino no encontrada"));
        TipoDocumento tipoDoc = req.getTipoDocumentoId() != null
                ? tipoDocRepo.findById(req.getTipoDocumentoId()).orElse(null) : null;

        Radicado r = Radicado.builder()
                .numeroRadicado(generador.generar(req.getTipo()))
                .tipo(req.getTipo())
                .asunto(req.getAsunto())
                .descripcion(req.getDescripcion())
                .tercero(tercero)
                .dependenciaDestino(destino)
                .usuarioRadica(quienRadica)
                .tipoDocumento(tipoDoc)
                .medioRecepcion(req.getMedioRecepcion())
                .estado("RADICADO")
                .build();
        repo.save(r);

        registrarHistorial(r, quienRadica, "RADICACION", null, destino,
                "Radicado creado tipo " + r.getTipo());

        if (destino.getJefe() != null) {
            notifService.enviar(destino.getJefe(),
                    "Nuevo radicado en tu dependencia",
                    "Radicado " + r.getNumeroRadicado() + ": " + r.getAsunto());
        }
        return toDto(r);
    }

    public Page<RadicadoResponse> listar(RadicadoFilter filter, Pageable p) {
        Specification<Radicado> spec = RadicadoSpecification.conFiltros(filter);
        return repo.findAll(spec, p).map(this::toDto);
    }

    public RadicadoResponse obtener(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Radicado no encontrado")));
    }

    public RadicadoResponse obtenerPorNumero(String numero) {
        return toDto(repo.findByNumeroRadicado(numero)
                .orElseThrow(() -> new ResourceNotFoundException("Radicado no encontrado")));
    }

    @Transactional
    public void asignar(Long id, AsignacionRequest req, Usuario quienAsigna) {
        Radicado r = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Radicado no encontrado"));
        Dependencia destino = null;
        if (req.getDependenciaId() != null) {
            destino = depRepo.findById(req.getDependenciaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dependencia no encontrada"));
            r.setDependenciaDestino(destino);
        }
        r.setEstado("ASIGNADO");
        repo.save(r);
        registrarHistorial(r, quienAsigna, "ASIGNACION",
                null, destino, req.getObservacion());
    }

    @Transactional
    public void cambiarEstado(Long id, CambioEstadoRequest req, Usuario quien) {
        Radicado r = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Radicado no encontrado"));
        r.setEstado(req.getNuevoEstado());
        repo.save(r);
        registrarHistorial(r, quien, "CAMBIO_ESTADO",
                null, r.getDependenciaDestino(),
                "Estado: " + req.getNuevoEstado() + " | " + (req.getObservacion() != null ? req.getObservacion() : ""));
    }

    public List<HistorialTrazabilidad> historial(Long id) {
        return histRepo.findByRadicadoIdOrderByFechaAsc(id);
    }

    private void registrarHistorial(Radicado r, Usuario u, String accion,
                                    Dependencia origen, Dependencia destino, String obs) {
        histRepo.save(HistorialTrazabilidad.builder()
                .radicado(r).usuario(u).accion(accion)
                .dependenciaOrigen(origen).dependenciaDestino(destino)
                .observacion(obs).build());
    }

    private RadicadoResponse toDto(Radicado r) {
        return RadicadoResponse.builder()
                .id(r.getId())
                .numeroRadicado(r.getNumeroRadicado())
                .tipo(r.getTipo())
                .fechaRadicacion(r.getFechaRadicacion())
                .asunto(r.getAsunto())
                .descripcion(r.getDescripcion())
                .tercero(r.getTercero() != null ? r.getTercero().getNombreRazonSocial() : null)
                .dependenciaDestino(r.getDependenciaDestino() != null ? r.getDependenciaDestino().getNombre() : null)
                .usuarioRadica(r.getUsuarioRadica() != null ? r.getUsuarioRadica().getNombre() : null)
                .tipoDocumento(r.getTipoDocumento() != null ? r.getTipoDocumento().getNombre() : null)
                .medioRecepcion(r.getMedioRecepcion())
                .estado(r.getEstado())
                .build();
    }
    public List<RespuestaResponse> listarRespuestas(Long radicadoId) {
        return respRepo.findByRadicadoId(radicadoId).stream()
                .map(r -> RespuestaResponse.builder()
                        .id(r.getId())
                        .contenido(r.getContenido())
                        .medioEnvio(r.getMedioEnvio())
                        .usuario(r.getUsuario() != null ? r.getUsuario().getNombre() : null)
                        .fechaRespuesta(r.getFechaRespuesta())
                        .build())
                .toList();
    }
}