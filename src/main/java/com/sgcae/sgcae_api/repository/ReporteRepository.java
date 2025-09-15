package com.sgcae.sgcae_api.repository;

import com.sgcae.sgcae_api.entity.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    List<Reporte> findByGeneradoPorIdUsuario(Long idUsuario);
}
