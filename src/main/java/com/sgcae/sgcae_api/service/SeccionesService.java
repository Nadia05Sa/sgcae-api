package com.sgcae.sgcae_api.service;

import com.sgcae.sgcae_api.entity.Secciones;
import com.sgcae.sgcae_api.repository.SeccionesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeccionesService {

    private final SeccionesRepository seccionesRepository;

    public SeccionesService(SeccionesRepository seccionesRepository) {
        this.seccionesRepository = seccionesRepository;
    }

    // Obtener todas las secciones
    public List<Secciones> findAll() {
        return seccionesRepository.findAll();
    }

    // Buscar sección por ID
    public Optional<Secciones> findById(Long idSeccion) {
        return seccionesRepository.findById(idSeccion);
    }

    // Guardar o actualizar sección
    public Secciones save(Secciones seccion) {
        return seccionesRepository.save(seccion);
    }

}
