package com.sgcae.sgcae_api.controller;

import com.sgcae.sgcae_api.service.PdfSeccionesService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pdf")
public class PdfSeccionesController {

    private final PdfSeccionesService pdfSeccionesService;

    public PdfSeccionesController(PdfSeccionesService pdfSeccionesService) {
        this.pdfSeccionesService = pdfSeccionesService;
    }

    @GetMapping("/secciones")
    public ResponseEntity<byte[]> generarPdfSecciones() {
        byte[] pdf = pdfSeccionesService.generarPdfSecciones();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=secciones.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
