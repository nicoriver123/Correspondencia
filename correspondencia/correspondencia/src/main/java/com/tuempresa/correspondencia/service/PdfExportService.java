package com.tuempresa.correspondencia.service;

import com.tuempresa.correspondencia.entity.Pqrs;
import com.tuempresa.correspondencia.entity.Radicado;
import com.tuempresa.correspondencia.exception.ResourceNotFoundException;
import com.tuempresa.correspondencia.repository.PqrsRepository;
import com.tuempresa.correspondencia.repository.RadicadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.Color; // <-- Importamos solo Color de java.awt
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

// Imports explícitos de OpenPDF (Lowagie) para evitar ambigüedad
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font; // <-- Aquí le decimos explícitamente que use este Font
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle; // <-- Y este Rectangle
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
@RequiredArgsConstructor
public class PdfExportService {
    private final RadicadoRepository radRepo;
    private final PqrsRepository pqrsRepo;

    public byte[] generarConstanciaRadicado(Long radicadoId) {
        Radicado r = radRepo.findById(radicadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Radicado no encontrado"));
        Pqrs pqrs = pqrsRepo.findByRadicadoId(radicadoId).orElse(null);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.LETTER, 40, 40, 40, 40);
            PdfWriter.getInstance(doc, out);
            doc.open();

            // Encabezado
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
            Paragraph titulo = new Paragraph("CONSTANCIA DE RADICADO", titleFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);
            doc.add(new Paragraph(" "));

            // Línea divisora
            PdfPTable line = new PdfPTable(1);
            line.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthBottom(2);
            cell.setBorderColor(Color.DARK_GRAY);
            line.addCell(cell);
            doc.add(line);
            doc.add(new Paragraph(" "));

            // Datos
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{35f, 65f});

            agregarFila(table, "Número de radicado:", r.getNumeroRadicado(), labelFont, valueFont);
            agregarFila(table, "Fecha y hora:", r.getFechaRadicacion() != null
                    ? r.getFechaRadicacion().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "", labelFont, valueFont);
            agregarFila(table, "Tipo:", r.getTipo(), labelFont, valueFont);
            agregarFila(table, "Asunto:", r.getAsunto(), labelFont, valueFont);
            agregarFila(table, "Descripción:", nvl(r.getDescripcion()), labelFont, valueFont);
            agregarFila(table, "Tercero:", r.getTercero() != null ? r.getTercero().getNombreRazonSocial() : "", labelFont, valueFont);
            agregarFila(table, "Identificación:", r.getTercero() != null ? nvl(r.getTercero().getNumeroIdentificacion()) : "", labelFont, valueFont);
            agregarFila(table, "Dependencia destino:", r.getDependenciaDestino() != null ? r.getDependenciaDestino().getNombre() : "", labelFont, valueFont);
            agregarFila(table, "Medio de recepción:", nvl(r.getMedioRecepcion()), labelFont, valueFont);
            agregarFila(table, "Estado:", r.getEstado(), labelFont, valueFont);

            if (pqrs != null) {
                doc.add(new Paragraph(" "));
                Paragraph sub = new Paragraph("INFORMACIÓN PQRS", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
                doc.add(sub);
                doc.add(new Paragraph(" "));
                agregarFila(table, "Tipo PQRS:", pqrs.getTipoPqrs(), labelFont, valueFont);
                agregarFila(table, "Categoría:", pqrs.getCategoria() != null ? pqrs.getCategoria().getNombre() : "", labelFont, valueFont);
                agregarFila(table, "Fecha límite respuesta:", pqrs.getFechaLimiteRespuesta() != null ? pqrs.getFechaLimiteRespuesta().toString() : "", labelFont, valueFont);
                agregarFila(table, "Días hábiles término:", String.valueOf(pqrs.getDiasHabilesTermino()), labelFont, valueFont);
                agregarFila(table, "Canal de entrada:", nvl(pqrs.getCanalEntrada()), labelFont, valueFont);
            }

            doc.add(table);

            // Pie con firma
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));
            Paragraph firma = new Paragraph("______________________________________");
            firma.setAlignment(Element.ALIGN_CENTER);
            doc.add(firma);
            Paragraph nombre = new Paragraph("Firma y sello de recepción");
            nombre.setAlignment(Element.ALIGN_CENTER);
            nombre.setFont(FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10));
            doc.add(nombre);

            doc.add(new Paragraph(" "));
            Paragraph aviso = new Paragraph(
                    "Este documento es una constancia de radicado. Conserve el número de radicado para consultar el estado.");
            aviso.setFont(FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, Color.GRAY));
            aviso.setAlignment(Element.ALIGN_CENTER);
            doc.add(aviso);

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage(), e);
        }
    }

    public byte[] generarResumenReporte() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.LETTER, 40, 40, 40, 40);
            PdfWriter.getInstance(doc, out);
            doc.open();
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph t = new Paragraph("RESUMEN EJECUTIVO - PQRS", titleFont);
            t.setAlignment(Element.ALIGN_CENTER);
            doc.add(t);
            doc.add(new Paragraph("Generado: " + java.time.LocalDate.now()));
            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void agregarFila(PdfPTable table, String label, String value, Font lf, Font vf) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, lf));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setPadding(5);
        PdfPCell c2 = new PdfPCell(new Phrase(value, vf));
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setPadding(5);
        table.addCell(c1);
        table.addCell(c2);
    }

    private String nvl(String s) {
        return s == null ? "" : s;
    }
}