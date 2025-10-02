package com.sgcae.sgcae_api.controller;

import com.sgcae.sgcae_api.entity.Secciones;
import com.sgcae.sgcae_api.service.SeccionesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/secciones")
public class SeccionesController {

    private final SeccionesService seccionesService;

    public SeccionesController(SeccionesService seccionesService) {
        this.seccionesService = seccionesService;
    }

    // Obtener todas las secciones
    @GetMapping
    public List<Secciones> getAllSecciones() {
        return seccionesService.findAll();
    }

    // Obtener una sección por ID
    @GetMapping("/{id}")
    public ResponseEntity<Secciones> getSeccionById(@PathVariable Long id) {
        return seccionesService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear nueva sección
    @PostMapping
    public Secciones createSeccion(@RequestBody Secciones seccion) {
        return seccionesService.save(seccion);
    }

    // Actualizar sección existente
    @PutMapping("/{id}")
    public ResponseEntity<Secciones> updateSeccion(@PathVariable Long id, @RequestBody Secciones seccionDetails) {
        return seccionesService.findById(id).map(seccion -> {
            seccion.setNumero_seccion(seccionDetails.getNumero_seccion());
            seccion.setColonias(seccionDetails.getColonias());
            seccion.setEncargado(seccionDetails.getEncargado());
            seccion.setMeta(seccionDetails.getMeta());
            Secciones updatedSeccion = seccionesService.save(seccion);
            return ResponseEntity.ok(updatedSeccion);
        }).orElse(ResponseEntity.notFound().build());
    }

}
