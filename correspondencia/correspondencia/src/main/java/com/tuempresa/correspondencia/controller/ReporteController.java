package com.tuempresa.correspondencia.controller;

import com.tuempresa.correspondencia.dto.ReporteConteo;
import com.tuempresa.correspondencia.service.ExcelExportService;
import com.tuempresa.correspondencia.service.PdfExportService;
import com.tuempresa.correspondencia.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','JEFE_DEPENDENCIA')")
public class ReporteController {
    private final ReporteService service;
    private final ExcelExportService excelService;
    private final PdfExportService pdfService;

    @GetMapping("/pqrs-por-tipo")
    public ResponseEntity<List<ReporteConteo>> porTipo() {
        return ResponseEntity.ok(service.porTipo());
    }

    @GetMapping("/cumplimiento-terminos")
    public ResponseEntity<Map<String, Object>> cumplimiento() {
        return ResponseEntity.ok(service.cumplimientoTerminos());
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar(@RequestParam(defaultValue = "xlsx") String formato) throws IOException {
        if ("pdf".equalsIgnoreCase(formato)) {
            byte[] pdf = pdfService.generarResumenReporte();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        }
        byte[] xls = excelService.exportarReporteCompleto();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(xls);
    }
}