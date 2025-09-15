package com.sgcae.sgcae_api.controller;

import com.sgcae.sgcae_api.entity.Reporte;
import com.sgcae.sgcae_api.service.CitaService;
import com.sgcae.sgcae_api.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private CitaService citaService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECRETARIA', 'ROLE_RECEPCION')")
    @GetMapping
    public List<Reporte> obtenerTodos() {
        return reporteService.obtenerTodos();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECRETARIA', 'ROLE_RECEPCION')")
    @GetMapping("/{id}")
    public Reporte obtenerPorId(@PathVariable Long id) {
        return reporteService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con id: " + id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SECRETARIA', 'ROLE_RECEPCION')")
    @PostMapping
    public Reporte crear(@RequestBody Reporte reporte) {
        Reporte nuevoReporte = reporteService.crear(reporte);
        citaService.marcarCitasComoEnReporte();
        return nuevoReporte;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public Reporte actualizar(@PathVariable Long id, @RequestBody Reporte reporte) {
        return reporteService.actualizar(id, reporte);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        reporteService.eliminar(id);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECRETARIA', 'ROLE_RECEPCION')")
    @GetMapping("/usuario/{idUsuario}")
    public List<Reporte> obtenerPorUsuario(@PathVariable Long idUsuario) {
        return reporteService.obtenerPorUsuario(idUsuario);
    }
}
