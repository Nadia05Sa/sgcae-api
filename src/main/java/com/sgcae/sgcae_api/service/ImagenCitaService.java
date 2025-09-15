package com.sgcae.sgcae_api.service;

import com.sgcae.sgcae_api.entity.ImagenCita;
import com.sgcae.sgcae_api.repository.ImagenCitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImagenCitaService {

    @Autowired
    private ImagenCitaRepository imagenCitaRepository;

    public ImagenCita guardar(ImagenCita imagenCita) {
        return imagenCitaRepository.save(imagenCita);
    }

    public List<ImagenCita> obtenerImagenesPorCita(Long idCita) {
        return imagenCitaRepository.findByCita_IdCita(idCita);
    }
}