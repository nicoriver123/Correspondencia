package com.tuempresa.correspondencia.service;

import com.tuempresa.correspondencia.entity.Pqrs;
import com.tuempresa.correspondencia.entity.Radicado;
import com.tuempresa.correspondencia.repository.PqrsRepository;
import com.tuempresa.correspondencia.repository.RadicadoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelExportService {
    private final PqrsRepository pqrsRepo;
    private final RadicadoRepository radRepo;

    public byte[] exportarReporteCompleto() throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            crearHojaPqrs(wb);
            crearHojaCumplimiento(wb);
            crearHojaRadicados(wb);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        }
    }

    private void crearHojaPqrs(XSSFWorkbook wb) {
        Sheet sheet = wb.createSheet("PQRS");
        CellStyle header = headerStyle(wb);

        Row title = sheet.createRow(0);
        Cell c = title.createCell(0);
        c.setCellValue("Reporte de PQRS - Generado " + LocalDate.now());
        c.setCellStyle(header);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));

        String[] cols = {"N° Radicado","Tipo PQRS","Categoría","Asunto","Tercero",
                "Dependencia","Estado","Fecha Límite","Días Restantes","Vencido"};
        Row h = sheet.createRow(2);
        for (int i = 0; i < cols.length; i++) {
            Cell cell = h.createCell(i);
            cell.setCellValue(cols[i]);
            cell.setCellStyle(header);
        }

        List<Pqrs> lista = pqrsRepo.findAll();
        int r = 3;
        for (Pqrs p : lista) {
            Row row = sheet.createRow(r++);
            row.createCell(0).setCellValue(p.getRadicado().getNumeroRadicado());
            row.createCell(1).setCellValue(p.getTipoPqrs());
            row.createCell(2).setCellValue(p.getCategoria() != null ? p.getCategoria().getNombre() : "");
            row.createCell(3).setCellValue(p.getRadicado().getAsunto());
            row.createCell(4).setCellValue(p.getRadicado().getTercero() != null
                    ? p.getRadicado().getTercero().getNombreRazonSocial() : "");
            row.createCell(5).setCellValue(p.getRadicado().getDependenciaDestino() != null
                    ? p.getRadicado().getDependenciaDestino().getNombre() : "");
            row.createCell(6).setCellValue(p.getRadicado().getEstado());
            row.createCell(7).setCellValue(p.getFechaLimiteRespuesta() != null
                    ? p.getFechaLimiteRespuesta().toString() : "");
            long dias = p.getFechaLimiteRespuesta() != null
                    ? java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), p.getFechaLimiteRespuesta()) : 0;
            row.createCell(8).setCellValue(dias);
            boolean vencido = LocalDate.now().isAfter(p.getFechaLimiteRespuesta())
                    && !"RESPONDIDO".equals(p.getRadicado().getEstado())
                    && !"CERRADO".equals(p.getRadicado().getEstado());
            row.createCell(9).setCellValue(vencido ? "SÍ" : "NO");
        }
        for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);
    }

    private void crearHojaCumplimiento(XSSFWorkbook wb) {
        Sheet sheet = wb.createSheet("Cumplimiento");
        CellStyle header = headerStyle(wb);
        Row h = sheet.createRow(0);
        String[] cols = {"Indicador","Valor"};
        for (int i = 0; i < cols.length; i++) {
            Cell c = h.createCell(i);
            c.setCellValue(cols[i]);
            c.setCellStyle(header);
        }
        List<Pqrs> todos = pqrsRepo.findAll();
        long total = todos.size();
        long vencidos = todos.stream().filter(p -> {
            String e = p.getRadicado().getEstado();
            return !("RESPONDIDO".equals(e) || "CERRADO".equals(e))
                    && p.getFechaLimiteRespuesta() != null
                    && LocalDate.now().isAfter(p.getFechaLimiteRespuesta());
        }).count();
        long aTiempo = total - vencidos;
        double pct = total == 0 ? 0 : (aTiempo * 100.0 / total);

        Row r1 = sheet.createRow(1); r1.createCell(0).setCellValue("Total PQRS"); r1.createCell(1).setCellValue(total);
        Row r2 = sheet.createRow(2); r2.createCell(0).setCellValue("A tiempo"); r2.createCell(1).setCellValue(aTiempo);
        Row r3 = sheet.createRow(3); r3.createCell(0).setCellValue("Vencidos"); r3.createCell(1).setCellValue(vencidos);
        Row r4 = sheet.createRow(4); r4.createCell(0).setCellValue("% Cumplimiento"); r4.createCell(1).setCellValue(pct);
        sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
    }

    private void crearHojaRadicados(XSSFWorkbook wb) {
        Sheet sheet = wb.createSheet("Radicados");
        CellStyle header = headerStyle(wb);
        String[] cols = {"N° Radicado","Tipo","Fecha","Asunto","Tercero","Dependencia","Estado"};
        Row h = sheet.createRow(0);
        for (int i = 0; i < cols.length; i++) {
            Cell c = h.createCell(i);
            c.setCellValue(cols[i]);
            c.setCellStyle(header);
        }
        List<Radicado> lista = radRepo.findAll();
        int r = 1;
        for (Radicado rd : lista) {
            Row row = sheet.createRow(r++);
            row.createCell(0).setCellValue(rd.getNumeroRadicado());
            row.createCell(1).setCellValue(rd.getTipo());
            row.createCell(2).setCellValue(rd.getFechaRadicacion() != null
                    ? rd.getFechaRadicacion().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "");
            row.createCell(3).setCellValue(rd.getAsunto());
            row.createCell(4).setCellValue(rd.getTercero() != null ? rd.getTercero().getNombreRazonSocial() : "");
            row.createCell(5).setCellValue(rd.getDependenciaDestino() != null ? rd.getDependenciaDestino().getNombre() : "");
            row.createCell(6).setCellValue(rd.getEstado());
        }
        for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);
    }

    private CellStyle headerStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        s.setFont(f);
        s.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return s;
    }
}