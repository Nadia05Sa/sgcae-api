package com.sgcae.sgcae_api.service;

import com.sgcae.sgcae_api.entity.Usuario;
import com.sgcae.sgcae_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario crear(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizar(Long id, Usuario nuevoUsuario) {
        return usuarioRepository.findById(id)
                .map(u -> {
                    u.setNombre(nuevoUsuario.getNombre());
                    u.setCorreo(nuevoUsuario.getCorreo());
                    u.setContrasena(nuevoUsuario.getContrasena());
                    u.setRol(nuevoUsuario.getRol());
                    u.setEstado(nuevoUsuario.getEstado());
                    return usuarioRepository.save(u);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Optional<Usuario> obtenerPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }
}
