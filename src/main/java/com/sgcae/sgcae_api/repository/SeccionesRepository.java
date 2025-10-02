package com.sgcae.sgcae_api.repository;

import com.sgcae.sgcae_api.entity.Secciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeccionesRepository extends JpaRepository<Secciones, Long> {

}
