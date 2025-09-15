package com.sgcae.sgcae_api.service;

import com.sgcae.sgcae_api.entity.Reporte;
import com.sgcae.sgcae_api.repository.ReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepository;

    public List<Reporte> obtenerTodos() {
        return reporteRepository.findAll();
    }

    public Optional<Reporte> obtenerPorId(Long id) {
        return reporteRepository.findById(id);
    }

    public Reporte crear(Reporte reporte) {
        return reporteRepository.save(reporte);
    }

    public Reporte actualizar(Long id, Reporte actualizado) {
        return reporteRepository.findById(id)
                .map(r -> {
                    r.setFechaInicio(actualizado.getFechaInicio());
                    r.setFechaFin(actualizado.getFechaFin());
                    r.setGeneradoPor(actualizado.getGeneradoPor());
                    return reporteRepository.save(r);
                })
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con id: " + id));
    }

    public void eliminar(Long id) {
        reporteRepository.deleteById(id);
    }

    public List<Reporte> obtenerPorUsuario(Long idUsuario) {
        return reporteRepository.findByGeneradoPorIdUsuario(idUsuario);
    }
}
