package com.sgcae.sgcae_api.service;

import com.sgcae.sgcae_api.entity.Cita;
import com.sgcae.sgcae_api.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CitaService {

    @Autowired
    private CitaRepository citaRepository;

    public List<Cita> obtenerTodas() {
        return citaRepository.findAll();
    }

    public Optional<Cita> obtenerPorId(Long id) {
        return citaRepository.findById(id);
    }

    public Cita crear(Cita cita) {
        return citaRepository.save(cita);
    }

    public Cita actualizar(Long id, Cita citaActualizada) {
        Optional<Cita> optionalCita = citaRepository.findById(id);
        if (optionalCita.isPresent()) {
            Cita citaExistente = optionalCita.get();
            citaExistente.setNombreCompleto(citaActualizada.getNombreCompleto());
            citaExistente.setCurp(citaActualizada.getCurp());
            citaExistente.setColonia(citaActualizada.getColonia());
            citaExistente.setTelefono(citaActualizada.getTelefono());
            citaExistente.setUsuario(citaActualizada.getUsuario());
            citaExistente.setApoyo(citaActualizada.getApoyo());
            citaExistente.setFotoIneUrl(citaActualizada.getFotoIneUrl());
            citaExistente.setEstado(citaActualizada.getEstado());
            citaExistente.setFechaAsignada(citaActualizada.getFechaAsignada());
            citaExistente.setHoraAsignada(citaActualizada.getHoraAsignada());
            return citaRepository.save(citaExistente);
        } else {
            throw new RuntimeException("Cita no encontrada con id: " + id);
        }
    }

    public void eliminar(Long id) {
        citaRepository.deleteById(id);
    }

    public void marcarCitasComoEnReporte() {
        List<Cita> pendientes = citaRepository.findAll()
                .stream()
                .filter(c -> c.getEstado() == Cita.Estado.PENDIENTE)
                .toList();

        for (Cita c : pendientes) {
            c.setEstado(Cita.Estado.EN_REPORTE);
        }

        citaRepository.saveAll(pendientes);
    }

    public List<Cita> obtenerHistorial() {
        return citaRepository.findByEstado(Cita.Estado.COMPLETADA);
    }

    public int contarApoyosPorCurp(String curp) {
        return citaRepository.countByCurpAndEstado(curp, Cita.Estado.COMPLETADA);
    }
}
