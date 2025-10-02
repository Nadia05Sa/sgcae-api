package com.sgcae.sgcae_api.controller;

import com.sgcae.sgcae_api.service.PdfCitaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pdf")
public class PdfCitaController {

    private final PdfCitaService pdfCitaService;

    public PdfCitaController(PdfCitaService pdfCitaService) {
        this.pdfCitaService = pdfCitaService;
    }

    @GetMapping("/citas-completadas")
    public ResponseEntity<byte[]> generarPdfCitasCompletadas() {
        byte[] pdf = pdfCitaService.generarPdfCitasCompletadas();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=citas_completadas.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
