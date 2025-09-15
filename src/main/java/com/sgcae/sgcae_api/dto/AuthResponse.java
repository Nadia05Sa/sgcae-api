package com.sgcae.sgcae_api.dto;

public class AuthResponse {
    private String token;
    private String nombre;
    private String rol;

    public AuthResponse(String token, String nombre, String rol) {
        this.token = token;
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRol() {
        return rol;
    }
}
