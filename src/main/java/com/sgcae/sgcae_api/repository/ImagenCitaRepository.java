package com.sgcae.sgcae_api.repository;

import com.sgcae.sgcae_api.entity.ImagenCita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImagenCitaRepository extends JpaRepository<ImagenCita, Long> {
    List<ImagenCita> findByCita_IdCita(Long idCita);
}
