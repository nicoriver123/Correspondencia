package com.tuempresa.correspondencia.service;

import com.tuempresa.correspondencia.dto.*;
import com.tuempresa.correspondencia.entity.*;
import com.tuempresa.correspondencia.exception.*;
import com.tuempresa.correspondencia.repository.*;
import com.tuempresa.correspondencia.specification.PqrsSpecification;
import com.tuempresa.correspondencia.util.CalculadoraDiasHabiles;
import com.tuempresa.correspondencia.util.GeneradorNumeroRadicado;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PqrsService {
    private final PqrsRepository pqrsRepo;
    private final RadicadoRepository radRepo;
    private final TerceroService terceroService;
    private final DependenciaRepository depRepo;
    private final CategoriaPqrsRepository catRepo;
    private final UsuarioRepository userRepo;
    private final HistorialTrazabilidadRepository histRepo;
    private final RespuestaRepository respRepo;
    private final GeneradorNumeroRadicado generador;
    private final CalculadoraDiasHabiles calcHabiles;
    private final NotificacionService notifService;
    private final EmailService emailService;

    private static final Map<String, Integer> TERMINOS_DIAS = Map.of(
            "PETICION", 15, "QUEJA", 15, "RECLAMO", 15,
            "SUGERENCIA", 10, "DENUNCIA", 10, "FELICITACION", 5
    );

    @Transactional
    public PqrsResponse radicar(PqrsRequest req, Usuario quienRadica) {
        Tercero tercero = terceroService.obtenerOCrear(
                req.getNombreTercero(), req.getIdentificacionTercero(),
                req.getTipoPersona(), req.getEmailTercero(),
                req.getTelefonoTercero(), req.getDireccionTercero());

        Dependencia destino = depRepo.findById(req.getDependenciaDestinoId())
                .orElseThrow(() -> new ResourceNotFoundException("Dependencia no encontrada"));
        CategoriaPqrs cat = req.getCategoriaId() != null ? catRepo.findById(req.getCategoriaId()).orElse(null) : null;

        int dias = obtenerTerminosDias(req.getTipoPqrs());
        LocalDate fechaLimite = calcHabiles.sumarDiasHabiles(LocalDate.now(), dias);

        Radicado r = Radicado.builder()
                .numeroRadicado(generador.generar("PQRS"))
                .tipo("ENTRADA")
                .asunto(req.getAsunto())
                .descripcion(req.getDescripcion())
                .tercero(tercero)
                .dependenciaDestino(destino)
                .usuarioRadica(quienRadica)
                .medioRecepcion(req.getCanalEntrada())
                .estado("RADICADO")
                .build();
        radRepo.save(r);

        Pqrs p = new Pqrs();
        p.setRadicado(r);
        p.setTipoPqrs(req.getTipoPqrs());
        p.setCategoria(cat);
        p.setDiasHabilesTermino(dias);
        p.setFechaLimiteRespuesta(fechaLimite);
        p.setCanalEntrada(req.getCanalEntrada());
        pqrsRepo.save(p);

        histRepo.save(HistorialTrazabilidad.builder()
                .radicado(r).usuario(quienRadica).accion("RADICACION_PQRS")
                .dependenciaDestino(destino)
                .observacion("PQRS tipo " + req.getTipoPqrs() + ". Vence: " + fechaLimite)
                .build());

        if (destino.getJefe() != null) {
            notifService.enviar(destino.getJefe(), "Nueva PQRS en tu dependencia", r.getNumeroRadicado() + " - " + req.getAsunto());
        }
        if (tercero.getEmail() != null && !tercero.getEmail().isBlank()) {
            emailService.notificarRadicacion(tercero.getEmail(), r.getNumeroRadicado(), r.getAsunto(), "PQRS");
        }

        return toDto(p);
    }

    private int obtenerTerminosDias(String tipo) {
        return TERMINOS_DIAS.getOrDefault(tipo.toUpperCase(), 15);
    }

    public Page<PqrsResponse> listar(PqrsFilter filter, Pageable p) {
        Specification<Pqrs> spec = PqrsSpecification.conFiltros(filter);
        return pqrsRepo.findAll(spec, p).map(this::toDto);
    }

    public PqrsResponse obtener(Long id, Usuario solicitante) {
        Pqrs p = pqrsRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PQRS no encontrada"));

        String rol = solicitante.getRol().getNombre();

        if ("FUNCIONARIO".equals(rol)) {
            boolean esSuyo = p.getUsuarioAsignado() != null
                    && p.getUsuarioAsignado().getId().equals(solicitante.getId());
            if (!esSuyo) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "No tiene acceso a esta PQRS.");
            }
        } else if ("JEFE_DEPENDENCIA".equals(rol)) {
            Long depUsuario = solicitante.getDependencia() != null ? solicitante.getDependencia().getId() : null;
            Long depPqrs = p.getRadicado().getDependenciaDestino() != null
                    ? p.getRadicado().getDependenciaDestino().getId() : null;
            if (depUsuario == null || !depUsuario.equals(depPqrs)) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "No tiene acceso a esta PQRS.");
            }
        }
        // ADMIN no se restringe.

        return toDto(p);
    }

    public PqrsResponse obtenerPorNumero(String numero) {
        Radicado r = radRepo.findByNumeroRadicado(numero)
                .orElseThrow(() -> new ResourceNotFoundException("Radicado no encontrado"));
        Pqrs p = pqrsRepo.findByRadicadoId(r.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No es una PQRS"));
        return toDto(p);
    }

    @Transactional
    public void asignar(Long id, AsignacionRequest req, Usuario quienAsigna) {
        Pqrs p = pqrsRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PQRS no encontrada"));

        if (req.getUsuarioId() != null) {
            // 1. PRIMERO se declara y busca la variable
            Usuario asignado = userRepo.findById(req.getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

            p.setUsuarioAsignado(asignado);
            notifService.enviar(asignado, "PQRS asignada", p.getRadicado().getNumeroRadicado() + " - " + p.getRadicado().getAsunto());

            // 2. DESPUÉS se usa
            if (asignado.getEmail() != null) {
                emailService.notificarAsignacion(asignado.getEmail(), p.getRadicado().getNumeroRadicado(), p.getRadicado().getAsunto());
            }
        }

        p.getRadicado().setEstado("EN_TRAMITE");
        radRepo.save(p.getRadicado());
        pqrsRepo.save(p);

        histRepo.save(HistorialTrazabilidad.builder()
                .radicado(p.getRadicado()).usuario(quienAsigna).accion("ASIGNACION_PQRS")
                .observacion(req.getObservacion()).build());
    }

    @Transactional
    public void responder(Long id, RespuestaRequest req, Usuario quienResponde) {
        Pqrs p = pqrsRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PQRS no encontrada"));

        Respuesta r = Respuesta.builder()
                .radicado(p.getRadicado())
                .usuario(quienResponde)
                .contenido(req.getContenido())
                .medioEnvio(req.getMedioEnvio())
                .build();

        if (req.getAnexoId() != null) {
            r.setAnexo(new Anexo());
            r.getAnexo().setId(req.getAnexoId());
        }

        respRepo.save(r);
        p.getRadicado().setEstado("RESPONDIDO");
        radRepo.save(p.getRadicado());

        // Notificación al tercero (corregido: sin duplicados y con llaves correctas)
        // Notificación al tercero (corregido: sin duplicados y con llaves correctas)
        if (p.getRadicado().getTercero() != null && p.getRadicado().getTercero().getEmail() != null) {
            emailService.notificarRespuesta(
                    p.getRadicado().getTercero().getEmail(),
                    p.getRadicado().getNumeroRadicado(),
                    p.getRadicado().getAsunto(),
                    req.getContenido()
            );
        }

        histRepo.save(HistorialTrazabilidad.builder()
                .radicado(p.getRadicado())
                .usuario(quienResponde)
                .accion("RESPUESTA")
                .observacion("Respuesta registrada")
                .build());
    }

    @Transactional
    public void reabrir(Long id, Usuario quien) {
        Pqrs p = pqrsRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PQRS no encontrada"));
        p.getRadicado().setEstado("EN_TRAMITE");
        radRepo.save(p.getRadicado());
        histRepo.save(HistorialTrazabilidad.builder()
                .radicado(p.getRadicado()).usuario(quien).accion("REAPERTURA")
                .observacion("PQRS reabierta").build());
    }

    private PqrsResponse toDto(Pqrs p) {
        Radicado r = p.getRadicado();
        LocalDate hoy = LocalDate.now();
        boolean vencido = hoy.isAfter(p.getFechaLimiteRespuesta())
                && !"RESPONDIDO".equals(r.getEstado())
                && !"CERRADO".equals(r.getEstado());
        int diasRest = (int) java.time.temporal.ChronoUnit.DAYS.between(hoy, p.getFechaLimiteRespuesta());

        return PqrsResponse.builder()
                .id(p.getId())
                .numeroRadicado(r.getNumeroRadicado())
                .tipoPqrs(p.getTipoPqrs())
                .categoria(p.getCategoria() != null ? p.getCategoria().getNombre() : null)
                .asunto(r.getAsunto())
                .descripcion(r.getDescripcion())
                .tercero(r.getTercero() != null ? r.getTercero().getNombreRazonSocial() : null)
                .dependenciaDestino(r.getDependenciaDestino() != null ? r.getDependenciaDestino().getNombre() : null)
                .usuarioAsignado(p.getUsuarioAsignado() != null ? p.getUsuarioAsignado().getNombre() : null)
                .estado(r.getEstado())
                .fechaLimiteRespuesta(p.getFechaLimiteRespuesta())
                .diasHabilesTermino(p.getDiasHabilesTermino())
                .vencido(vencido)
                .diasRestantes(diasRest)
                .canalEntrada(p.getCanalEntrada())
                .fechaRadicacion(r.getFechaRadicacion())
                .radicadoId(r.getId())
                .build();
    }
    public List<PqrsResponse> listarVencidos() {
        return pqrsRepo.findVencidos().stream().map(this::toDto).toList();
    }

    public List<PqrsResponse> listarProximosAVencer() {
        LocalDate fechaHasta = LocalDate.now().plusDays(3);
        return pqrsRepo.findProximosAVencer(fechaHasta).stream().map(this::toDto).toList();
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
