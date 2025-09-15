package com.sgcae.sgcae_api.service;

import com.sgcae.sgcae_api.entity.Apoyo;
import com.sgcae.sgcae_api.repository.ApoyoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApoyoService {

    @Autowired
    private ApoyoRepository apoyoRepository;

    public List<Apoyo> obtenerTodos() {
        return apoyoRepository.findAll();
    }

    public Optional<Apoyo> obtenerPorId(Long id) {
        return apoyoRepository.findById(id);
    }

    public Apoyo crear(Apoyo apoyo) {
        return apoyoRepository.save(apoyo);
    }

    public Apoyo actualizar(Long id, Apoyo nuevoApoyo) {
        return apoyoRepository.findById(id)
                .map(apoyo -> {
                    apoyo.setTipo(nuevoApoyo.getTipo());
                    apoyo.setMonto(nuevoApoyo.getMonto());
                    return apoyoRepository.save(apoyo);
                })
                .orElseThrow(() -> new RuntimeException("Apoyo no encontrado con id: " + id));
    }

    public void eliminar(Long id) {
        apoyoRepository.deleteById(id);
    }

    // ✅ NUEVO: Buscar por nombre (tipo)
    public Optional<Apoyo> buscarPorNombre(String tipo) {
        return apoyoRepository.findByTipo(tipo);
    }
}
