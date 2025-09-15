package com.sgcae.sgcae_api.controller;

import com.sgcae.sgcae_api.dto.CitaDTO;
import com.sgcae.sgcae_api.entity.Apoyo;
import com.sgcae.sgcae_api.entity.Cita;
import com.sgcae.sgcae_api.entity.ImagenCita;
import com.sgcae.sgcae_api.service.ApoyoService;
import com.sgcae.sgcae_api.service.CitaService;
import com.sgcae.sgcae_api.service.ImagenCitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173") // Asegura CORS para el frontend en Vite
@RestController
@RequestMapping("/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private ApoyoService apoyoService;

    @Autowired
    private ImagenCitaService imagenCitaService;

    // Obtener todas las citas como DTO (con imágenes)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECRETARIA', 'ROLE_RECEPCION')")
    @GetMapping
    public List<CitaDTO> obtenerTodas() {
        List<Cita> citas = citaService.obtenerTodas();
        List<CitaDTO> dtos = new ArrayList<>();

        for (Cita cita : citas) {
            CitaDTO dto = new CitaDTO();
            dto.setIdCita(cita.getIdCita());
            dto.setNombreCompleto(cita.getNombreCompleto());
            dto.setCurp(cita.getCurp());
            dto.setTelefono(cita.getTelefono());
            dto.setEstado(cita.getEstado().toString());
            dto.setTipoApoyo(cita.getApoyo().getTipo());
            dto.setColonia(cita.getColonia());
            dto.setFotoIneUrl(cita.getFotoIneUrl());

            List<String> rutas = imagenCitaService.obtenerImagenesPorCita(cita.getIdCita())
                    .stream()
                    .map(ImagenCita::getRutaImagen)
                    .toList();

            dto.setImagenes(rutas);
            dtos.add(dto);
        }

        return dtos;
    }

    // Obtener una cita específica
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECRETARIA', 'ROLE_RECEPCION')")
    @GetMapping("/{id}")
    public Cita obtenerPorId(@PathVariable Long id) {
        return citaService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con id: " + id));
    }

    // Crear cita con imágenes
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECRETARIA', 'ROLE_RECEPCION')")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Cita> crearConImagen(
            @RequestPart("nombreCompleto") String nombreCompleto,
            @RequestPart("curp") String curp,
            @RequestPart("telefono") String telefono,
            @RequestPart("estado") String estado,
            @RequestPart("tipoApoyo") String tipoApoyo,
            @RequestPart("colonia") String colonia,
            @RequestPart("imagenes") List<MultipartFile> imagenes) {

        try {
            Apoyo apoyoExistente = apoyoService.buscarPorNombre(tipoApoyo)
                    .orElseGet(() -> {
                        Apoyo nuevo = new Apoyo();
                        nuevo.setTipo(tipoApoyo);
                        return apoyoService.crear(nuevo);
                    });

            Cita cita = new Cita();
            cita.setNombreCompleto(nombreCompleto);
            cita.setCurp(curp);
            cita.setTelefono(telefono);
            cita.setColonia(colonia);
            cita.setEstado(Cita.Estado.valueOf(estado));
            cita.setApoyo(apoyoExistente);

            cita = citaService.crear(cita);

            String basePath = "C:/sgcae/imagenes";
            File carpeta = new File(basePath);
            if (!carpeta.exists())
                carpeta.mkdirs();

            for (int i = 0; i < imagenes.size(); i++) {
                MultipartFile imagen = imagenes.get(i);
                if (!imagen.isEmpty()) {
                    String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
                    File destino = new File(basePath + "/" + nombreArchivo);
                    imagen.transferTo(destino);

                    ImagenCita nueva = new ImagenCita();
                    nueva.setRutaImagen(nombreArchivo);
                    nueva.setCita(cita);
                    imagenCitaService.guardar(nueva);

                    if (i == 0) {
                        cita.setFotoIneUrl(nombreArchivo);
                        citaService.actualizar(cita.getIdCita(), cita);
                    }
                }
            }

            return ResponseEntity.ok(cita);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // Actualizar cita
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public Cita actualizar(@PathVariable Long id, @RequestBody Cita cita) {
        return citaService.actualizar(id, cita);
    }

    // Eliminar cita
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        citaService.eliminar(id);
    }

    // ⚠️ Método crítico que da error 403 si el rol no coincide
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECRETARIA', 'ROLE_RECEPCION')")
    @PutMapping("/marcar-en-reporte")
    public ResponseEntity<Void> marcarCitasComoEnReporte() {
        System.out.println(
                "🔍 Usuario autenticado en el contexto: " + SecurityContextHolder.getContext().getAuthentication());
        citaService.marcarCitasComoEnReporte();
        return ResponseEntity.ok().build();
    }

    // Historial de citas completadas
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECRETARIA', 'ROLE_RECEPCION')")
    @GetMapping("/historial")
    public List<Cita> obtenerHistorial() {
        return citaService.obtenerHistorial();
    }

    // Verifica cuántos apoyos ha recibido un CURP
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECRETARIA', 'ROLE_RECEPCION')")
    @GetMapping("/contar-por-curp/{curp}")
    public ResponseEntity<Integer> contarApoyosPorCurp(@PathVariable String curp) {
        int total = citaService.contarApoyosPorCurp(curp);
        return ResponseEntity.ok(total);
    }

    // Obtener imágenes asociadas a una cita
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SECRETARIA', 'ROLE_RECEPCION')")
    @GetMapping("/{id}/imagenes")
    public ResponseEntity<List<String>> obtenerImagenesDeCita(@PathVariable Long id) {
        List<ImagenCita> imagenes = imagenCitaService.obtenerImagenesPorCita(id);
        List<String> rutas = imagenes.stream()
                .map(ImagenCita::getRutaImagen)
                .toList();
        return ResponseEntity.ok(rutas);
    }
}
