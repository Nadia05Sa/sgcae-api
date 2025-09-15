package com.sgcae.sgcae_api.repository;

import com.sgcae.sgcae_api.entity.Apoyo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApoyoRepository extends JpaRepository<Apoyo, Long> {
    Optional<Apoyo> findByTipo(String tipo); // ✅ NUEVO
}
