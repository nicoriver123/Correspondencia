package com.tuempresa.correspondencia.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.sistema-nombre}") private String sistemaNombre;
    @Value("${app.frontend-url}") private String frontendUrl;

    @Async
    public void enviar(String destinatario, String asunto, String cuerpoHtml) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
            h.setFrom("no-reply@tuempresa.com");
            h.setTo(destinatario);
            h.setSubject(asunto);
            h.setText(cuerpoHtml, true);
            mailSender.send(msg);
            log.info("📧 Correo enviado a {}: {}", destinatario, asunto);
        } catch (MessagingException e) {
            log.error("❌ Error enviando correo a {}: {}", destinatario, e.getMessage());
        }
    }

    // === Plantillas ===

    public void notificarRadicacion(String emailDestino, String numeroRadicado, String asunto, String tipo) {
        String html = plantilla(
                "Radicado creado exitosamente",
                "Se ha radicado el documento <b>" + numeroRadicado + "</b> tipo <b>" + tipo + "</b>.",
                "Asunto: " + escaparHtml(asunto),
                "Puede consultar el estado en cualquier momento con su número de radicado."
        );
        enviar(emailDestino, "✅ Radicado " + numeroRadicado + " creado", html);
    }

    public void notificarAsignacion(String emailDestino, String numeroRadicado, String asunto) {
        String html = plantilla(
                "Nueva tarea asignada",
                "Se le ha asignado el radicado <b>" + numeroRadicado + "</b>.",
                "Asunto: " + escaparHtml(asunto),
                "Por favor gestione a la mayor brevedad."
        );
        enviar(emailDestino, "📋 Radicado asignado: " + numeroRadicado, html);
    }

    public void notificarRespuesta(String emailDestino, String numeroRadicado, String asunto, String contenidoRespuesta) {
        String bloqueRespuesta = "";
        if (contenidoRespuesta != null && !contenidoRespuesta.isBlank()) {
            String contenidoHtml = escaparHtml(contenidoRespuesta).replace("\n", "<br>");
            bloqueRespuesta =
                    "<div style='background:#f3f4f6;border-left:4px solid #2563eb;padding:15px;margin:15px 0;border-radius:4px;'>"
                            + "<p style='color:#1f2937;margin:0;line-height:1.6;'>" + contenidoHtml + "</p>"
                            + "</div>";
        }

        String html = plantilla(
                "Su solicitud ha sido respondida",
                "El radicado <b>" + numeroRadicado + "</b> ya cuenta con respuesta formal.",
                asunto != null ? "Asunto: " + escaparHtml(asunto) : "",
                bloqueRespuesta,
                "Si tiene alguna inquietud adicional, puede contactarnos indicando su número de radicado."
        );
        enviar(emailDestino, "✅ Respuesta al radicado " + numeroRadicado, html);
    }

    public void notificarProximoVencimiento(String emailDestino, String numeroRadicado, long diasRestantes) {
        String color = diasRestantes <= 1 ? "#d9534f" : "#f0ad4e";
        String html = plantilla(
                "⚠️ PQRS próxima a vencer",
                "El radicado <b>" + numeroRadicado + "</b> vence en <b style='color:" + color + "'>"
                        + diasRestantes + " día(s)</b>.",
                "",
                "Por favor priorice su gestión."
        );
        enviar(emailDestino, "⚠️ PQRS " + numeroRadicado + " próxima a vencer", html);
    }

    // Overload sin bloque extra, para no tocar las plantillas que ya funcionan
    private String plantilla(String titulo, String mensaje, String detalle, String cierre) {
        return plantilla(titulo, mensaje, detalle, "", cierre);
    }

    private String plantilla(String titulo, String mensaje, String detalle, String bloqueExtra, String cierre) {
        return "<!DOCTYPE html><html><body style='font-family:Arial,sans-serif;background:#f4f4f4;padding:20px;'>"
                + "<div style='max-width:600px;margin:auto;background:white;border-radius:8px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,0.1);'>"
                + "<div style='background:#2563eb;color:white;padding:20px;'>"
                + "<h2 style='margin:0;'>" + sistemaNombre + "</h2></div>"
                + "<div style='padding:25px;'>"
                + "<h3 style='color:#1f2937;'>" + titulo + "</h3>"
                + "<p style='color:#4b5563;line-height:1.5;'>" + mensaje + "</p>"
                + (detalle != null && !detalle.isEmpty() ? "<p style='color:#4b5563;'><i>" + detalle + "</i></p>" : "")
                + bloqueExtra
                + "<p style='color:#4b5563;'>" + cierre + "</p>"
                + "<a href='" + frontendUrl + "' style='display:inline-block;background:#2563eb;color:white;padding:10px 20px;text-decoration:none;border-radius:5px;margin-top:10px;'>Ir al portal</a>"
                + "</div>"
                + "<div style='background:#f9fafb;padding:15px;text-align:center;color:#6b7280;font-size:12px;'>"
                + "Este es un correo automático, por favor no responda.</div></div></body></html>";
    }

    // Evita que el texto libre que escribe un funcionario rompa el HTML del
    // correo (o inyecte etiquetas) si por ejemplo escribe un "<" en la respuesta.
    private String escaparHtml(String texto) {
        if (texto == null) return "";
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}