package com.sgcae.sgcae_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "imagenes_cita")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagenCita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idImagen;

    private String rutaImagen;

    @ManyToOne
    @JoinColumn(name = "id_cita")
    private Cita cita;
}
