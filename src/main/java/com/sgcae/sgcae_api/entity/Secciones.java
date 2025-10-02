package com.sgcae.sgcae_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Secciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Secciones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSeccion;

    int numero_seccion;
    String colonias;
    String encargado;
    int meta;
}
