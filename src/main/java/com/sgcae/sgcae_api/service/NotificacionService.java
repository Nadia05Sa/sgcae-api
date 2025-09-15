package com.sgcae.sgcae_api.service;

import com.sgcae.sgcae_api.entity.Notificacion;
import com.sgcae.sgcae_api.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    public List<Notificacion> obtenerTodas() {
        return notificacionRepository.findAll();
    }

    public Optional<Notificacion> obtenerPorId(Long id) {
        return notificacionRepository.findById(id);
    }

    public Notificacion crear(Notificacion notificacion) {
        return notificacionRepository.save(notificacion);
    }

    public Notificacion actualizar(Long id, Notificacion actualizada) {
        return notificacionRepository.findById(id)
                .map(notificacion -> {
                    notificacion.setTipo(actualizada.getTipo());
                    notificacion.setMensaje(actualizada.getMensaje());
                    notificacion.setLeido(actualizada.isLeido());
                    notificacion.setParaRol(actualizada.getParaRol());
                    return notificacionRepository.save(notificacion);
                })
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada con id: " + id));
    }

    public void eliminar(Long id) {
        notificacionRepository.deleteById(id);
    }

    public Notificacion marcarComoLeida(Long id) {
        return notificacionRepository.findById(id)
                .map(n -> {
                    n.setLeido(true);
                    return notificacionRepository.save(n);
                })
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada con id: " + id));
    }

    public List<Notificacion> obtenerPorRol(String rol) {
        try {
            Notificacion.Rol rolEnum = Notificacion.Rol.valueOf(rol);
            return notificacionRepository.findByParaRol(rolEnum);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rol inválido: " + rol);
        }
    }
}
