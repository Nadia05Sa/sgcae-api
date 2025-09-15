package com.sgcae.sgcae_api.controller;

import com.sgcae.sgcae_api.entity.Apoyo;
import com.sgcae.sgcae_api.service.ApoyoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/apoyos")
public class ApoyoController {

    @Autowired
    private ApoyoService apoyoService;

    @GetMapping
    public List<Apoyo> obtenerTodos() {
        return apoyoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Apoyo obtenerPorId(@PathVariable Long id) {
        return apoyoService.obtenerPorId(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Apoyo no encontrado con id: " + id));
    }

    @PostMapping
    public Apoyo crear(@RequestBody Apoyo apoyo) {
        return apoyoService.crear(apoyo);
    }

    @PutMapping("/{id}")
    public Apoyo actualizar(@PathVariable Long id, @RequestBody Apoyo apoyo) {
        return apoyoService.actualizar(id, apoyo);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        apoyoService.eliminar(id);
    }
}
