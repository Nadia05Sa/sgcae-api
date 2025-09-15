package com.sgcae.sgcae_api.repository;

import com.sgcae.sgcae_api.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByEstado(Cita.Estado estado);

    List<Cita> findByNombreCompletoAndEstado(String nombreCompleto, Cita.Estado estado);

    int countByCurpAndEstado(String curp, Cita.Estado estado); // NUEVO: para contar apoyos por CURP
}
