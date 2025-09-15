package com.sgcae.sgcae_api.dto;

import lombok.Data;

import java.util.List;

@Data
public class CitaDTO {
    private Long idCita;
    private String nombreCompleto;
    private String curp;
    private String telefono;
    private String estado;
    private String tipoApoyo;
    private String colonia;
    private String fotoIneUrl;
    private List<String> imagenes;
}
