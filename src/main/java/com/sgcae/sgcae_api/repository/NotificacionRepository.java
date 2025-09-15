package com.sgcae.sgcae_api.repository;

import com.sgcae.sgcae_api.entity.Notificacion;
import com.sgcae.sgcae_api.entity.Notificacion.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByParaRol(Rol rol);
}
