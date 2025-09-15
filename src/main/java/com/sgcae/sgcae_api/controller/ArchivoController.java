package com.sgcae.sgcae_api.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/archivos/ine")
@CrossOrigin(origins = "http://localhost:5173")
public class ArchivoController {

    private final String basePath = "C:/sgcae/imagenes"; // Mismo path que en CitaController

    @GetMapping("/{nombreArchivo}")
    public ResponseEntity<Resource> servirArchivo(@PathVariable String nombreArchivo) {
        try {
            Path archivoPath = Paths.get(basePath).resolve(nombreArchivo).normalize();
            Resource recurso = new UrlResource(archivoPath.toUri());

            if (!recurso.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"")
                    .body(recurso);

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
