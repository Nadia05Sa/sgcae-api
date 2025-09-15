package com.sgcae.sgcae_api.controller;

import com.sgcae.sgcae_api.entity.Notificacion;
import com.sgcae.sgcae_api.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECRETARIA', 'ROLE_RECEPCION')")
    @GetMapping
    public List<Notificacion> obtenerTodas() {
        return notificacionService.obtenerTodas();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECRETARIA', 'ROLE_RECEPCION')")
    @GetMapping("/{id}")
    public Notificacion obtenerPorId(@PathVariable Long id) {
        return notificacionService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada con id: " + id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECRETARIA', 'ROLE_RECEPCION')")
    @PostMapping
    public Notificacion crear(@RequestBody Notificacion notificacion) {
        try {
            System.out.println("Entró al controlador de POST /notificaciones");
            System.out.println("Tipo recibido: " + notificacion.getTipo());
            System.out.println("Rol recibido: " + notificacion.getParaRol());

            return notificacionService.crear(notificacion);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error al crear notificación: Enum inválido. Verifica que los valores de 'tipo' y 'paraRol' sean correctos.");
        } catch (Exception ex) {
            throw new RuntimeException("Error general al crear notificación: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECRETARIA', 'ROLE_RECEPCION')")
    @PutMapping("/{id}")
    public Notificacion actualizar(@PathVariable Long id, @RequestBody Notificacion notificacion) {
        return notificacionService.actualizar(id, notificacion);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        notificacionService.eliminar(id);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECRETARIA', 'ROLE_RECEPCION')")
    @PutMapping("/{id}/leer")
    public Notificacion marcarComoLeida(@PathVariable Long id) {
        return notificacionService.marcarComoLeida(id);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECRETARIA', 'ROLE_RECEPCION')")
    @GetMapping("/rol/{rol}")
    public List<Notificacion> obtenerPorRol(@PathVariable String rol) {
        return notificacionService.obtenerPorRol(rol.toUpperCase());
    }

    @ExceptionHandler(Exception.class)
    public String manejarErrores(Exception e) {
        e.printStackTrace();
        return "Error: " + e.getMessage();
    }
}
